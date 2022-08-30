package wfm.shared.excel

import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellValue
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.HttpHeaders
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

fun String.workbook(windowSize: Int? = null, block: ExcelWorkbook.() -> Unit): ExcelWorkbook =
    ExcelWorkbook(name = this, workbook = windowSize?.let { SXSSFWorkbook(it) } ?: XSSFWorkbook())
        .apply { block() }
        .apply { finalize() }

fun workbook(windowSize: Int? = null, block: ExcelWorkbook.() -> Unit): ExcelWorkbook =
    "Workbook1".workbook(windowSize, block)

class ExcelWorkbook(val name: String, val workbook: Workbook) {
    val sheets: MutableMap<String, ExcelSheet> = mutableMapOf()
    var formulaEvaluator: FormulaEvaluator = workbook.creationHelper.createFormulaEvaluator()

    fun native(block: Workbook.() -> Unit): Unit = workbook.block()
    fun init(block: () -> Unit) = block()
    fun finalize() {
        workbook.createDataFormat()
    }

    // Sheet -----------------------------------------------------------------------------------------------------------
    fun String.sheet(block: ExcelSheet.() -> Unit): ExcelSheet =
        ExcelSheet(name = this, sheet = workbook.createSheet(this))
            .also { sheets[this] = it }
            .apply { block() }
            .apply { finalize() }

    inner class ExcelSheet(val name: String, val sheet: Sheet) {
        val rows: MutableList<ExcelRow<out Any>> = mutableListOf()
        var validators: MutableMap<Any, MutableSet<ExcelCellValidator>> = mutableMapOf()

        fun native(block: Sheet.() -> Unit): Unit = sheet.block()
        fun init(block: () -> Unit) = block()
        fun finalize() {
        }

        fun protect(password: String) {
            sheet.protectSheet(password)
        }

        // Row ---------------------------------------------------------------------------------------------------------
        // 단일 Row 생성
        fun Int.row(block: ExcelRow<Any>.() -> Unit): ExcelRow<Any> =
            ExcelRow<Any>(row = sheet.createRow(this.rowIndex), value = Any())
                .also(rows::add)
                .apply { block() }
                .apply { finalize() }

        // 다중 Row 생성
        fun <T : Any> Int.rows(value: List<T>, block: ExcelRow<T>.(data: T) -> Unit): List<ExcelRow<T>> =
            value.mapIndexed { index, element ->
                ExcelRow<T>(row = sheet.createRow(this.rowIndex + index), value = element)
                    .also(rows::add)
                    .apply { block(element) }
                    .apply { finalize(element) }
            }

        // Row Builder
        inner class ExcelRow<T>(val row: Row, var value: T) {
            val cols: MutableList<ExcelCol> = mutableListOf()
            var style: CellStyle? = null
            var type: ExcelCellType? = null

            fun ExcelCellType.type() {
                type = this
                row.height = 10
            }

            fun Int.height() {
                row.height = (this * ROW_HEIGHT_TWIPS_PER_PIXEL).toShort()
            }

            fun ExcelStyle.style() {
                style = styles.style(name)
            }

            fun String.style() {
                style = styles.style(this)
            }

            fun native(block: Row.() -> Unit): Unit = row.block()
            fun init(block: () -> Unit) = block()
            fun finalize(data: T? = null) {
            }

            // Col -----------------------------------------------------------------------------------------------------
            fun cols(block: (data: T) -> Unit) = block(value)

            // A-based 단일 Cell 생성
            fun String.col(value: Any? = null, block: (ExcelCol.() -> Unit)? = null): ExcelCol =
                colNumber.col(value, block)

            // 1-based 단일 Cell 생성
            fun Int.col(value: Any? = null, block: (ExcelCol.() -> Unit)? = null): ExcelCol =
                ExcelCol(cell = row.createCell(colIndex), value)
                    .also(cols::add)
                    .apply { block?.let { block() } }
                    .apply { finalize() }

            // Cell 생성 Builder
            inner class ExcelCol(val cell: Cell, var value: Any? = null) {
                private var formula: String? = null
                private var style: CellStyle? = this@ExcelRow.style
                private var format: Short = workbook.createDataFormat().getFormat("@")
                private var type: ExcelCellType? = this@ExcelRow.type
                private var cellValidators: MutableSet<ExcelCellValidator> = mutableSetOf()
                private var colValidators: MutableSet<ExcelCellValidator> = mutableSetOf()

                fun Any.value() {
                    value = this
                }

                fun Int.width() {
                    sheet.setColumnWidth(cell.columnIndex, this * COLUMN_WIDTH_PER_CHAR)
                }

                fun hide() {
                    sheet.setColumnHidden(cell.columnIndex, true)
                }

                fun ExcelStyle.style() {
                    style = styles.style(name)
                }

                fun String.style() {
                    style = styles.style(this)
                }

                // CellType 을 CellType.FORMULA 로 변경
                fun String.formula() {
                    formula = this
                }

                fun formula(): CellValue = formulaEvaluator.evaluate(cell)

                fun String.format() {
                    format = workbook.createDataFormat().getFormat(this)
                }

                fun ExcelCellType.type() {
                    type = this
                }

                fun ExcelCellValidator.cellValidator() {
                    cellValidators.add(this)
                }

                fun ExcelCellValidator.colValidator() {
                    colValidators.add(this)
                }

                fun native(block: Cell.() -> Unit): Unit = cell.block()
                fun init(block: () -> Unit) = block()
                fun finalize() {
                    cell.apply {
                        formula
                            ?.let { cellFormula = it }
                            ?.run { formulaEvaluator.evaluateFormulaCell(cell) }
                        when (value) {
                            is String -> setCellValue(value.toString())
                            is Number -> setCellValue((value as Number).toDouble())
                            is LocalDate -> setCellValue((value as LocalDate).format(DATE_FORMATTER))
                            is LocalDateTime -> setCellValue((value as LocalDateTime).format(DATETIME_FORMATTER))
                            is Boolean -> setCellValue(value as Boolean)
                            is RichTextString -> setCellValue(value as RichTextString)
                            else -> setBlank()
                        }
                        style?.let { cellStyle = it }
                        cellStyle.dataFormat = format
                    }

                    type?.let {
                        cellValidators.add(ExcelCellValidator.Type(type!!))
                        colValidators.add(ExcelCellValidator.Type(type!!))
                    }
                    if (cellValidators.isNotEmpty()) {
                        this@ExcelSheet.validators[cell.address] = cellValidators
                    }
                    if (colValidators.isNotEmpty()) {
                        this@ExcelSheet.validators[cell.address.column] = colValidators
                    }
                }
            }
        }
    }

    val styles = ExcelStyleMap(workbook)
    fun styles(block: ExcelStyleMap.() -> Unit): Unit = styles.block()
    fun String.style(): CellStyle? = styles.style(this)
    fun ExcelStyle.style(): CellStyle? = name.style()

    companion object {
        const val COLUMN_WIDTH_PER_CHAR: Int = 256 + 64
        const val ROW_HEIGHT_TWIPS_PER_PIXEL: Int = 20
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    }
}

enum class ExcelCellType {
    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    LOCALDATE,
    LOCALDATETIME,
    BOOLEAN,
}

fun ExcelWorkbook.download(response: HttpServletResponse) {
    response
        .apply {
            contentType = "application/vnd.ms-excel"
            setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=$name.xlsx".encodeUTF8()
            )
        }
        .run {
            outputStream
                .use { stream ->
                    workbook.use { it.write(stream) }
                }
        }
}

fun Workbook.download(response: HttpServletResponse, name: String) {
    response
        .apply {
            contentType = "application/vnd.ms-excel"
            setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=$name.xlsx".encodeUTF8()
            )
        }
        .outputStream
        .use { stream ->
            this@download.use { it.write(stream) }
        }
}

private fun String.encodeUTF8(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8)
