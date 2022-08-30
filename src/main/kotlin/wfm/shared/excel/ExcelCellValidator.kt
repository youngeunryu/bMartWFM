package wfm.shared.excel

import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook

private val log = KotlinLogging.logger {}

sealed class ExcelCellValidator {
    abstract fun isValid(target: Cell, block: (Cell.() -> String?)? = null): String?

    class Equal(private val origin: Cell) : ExcelCellValidator() {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? =
            when (origin.cellType) {
                CellType.STRING -> origin.stringCellValue == target.stringCellValue
                CellType.NUMERIC -> origin.numericCellValue == target.numericCellValue
                CellType.BOOLEAN -> origin.booleanCellValue == target.booleanCellValue
                else -> false
            }
                .takeUnless { it }
                ?.let { "값이 일치하지 않습니다." }
    }

    object Required : ExcelCellValidator() {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? =
            when (target.cellType) {
                CellType.STRING -> target.stringCellValue.isNotEmpty()
                CellType.BLANK -> false
                else -> true
            }
                .takeUnless { it }
                ?.let { "필수 항목입니다." }
    }

    class Type(private val type: ExcelCellType) : ExcelCellValidator() {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? =
            when (type) {
                ExcelCellType.STRING -> target.cellType == CellType.STRING
                ExcelCellType.INTEGER -> target.cellType == CellType.NUMERIC
                ExcelCellType.LONG -> target.cellType == CellType.NUMERIC
                ExcelCellType.DOUBLE -> target.cellType == CellType.NUMERIC
                ExcelCellType.LOCALDATE -> target.cellType == CellType.STRING && target.runCatching { localDateTimeCellValue }.isSuccess
                ExcelCellType.LOCALDATETIME -> target.cellType == CellType.STRING && target.runCatching { localDateTimeCellValue }.isSuccess
                ExcelCellType.BOOLEAN -> target.cellType == CellType.BOOLEAN
            }
                .takeUnless { it }
                ?.let { "데이터 유형이 일치하지 않습니다." }
    }

    class Length(val min: Int? = null, val max: Int? = null) : ExcelCellValidator() {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? =
            target.run {
                if (cellType == CellType.NUMERIC) false
                else {
                    val length = stringCellValue.length
                    if (min != null && max != null) min <= length && length <= max
                    else if (min != null) length >= min
                    else if (max != null) length <= max
                    else false
                }
            }
                .takeUnless { it }
                ?.let { "문자열 데이터의 길이가 주어진 범위를 초과합니다. [min=$min, max=$max]" }
    }

    class Range<T>(val min: T? = null, val max: T? = null) :
        ExcelCellValidator() where T : Number, T : Comparable<T> {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? =
            target.run {
                if (cellType != CellType.NUMERIC) false
                else {
                    val value = numericCellValue
                    if (min != null && max != null) min.toDouble() <= value && value <= max.toDouble()
                    else if (min != null) value >= min.toDouble()
                    else if (max != null) value <= max.toDouble()
                    else false
                }
            }
                .takeUnless { it }
                ?.let { "데이터 값이 주어진 범위를 초과합니다. [min=$min, max=$max]" }
    }

    object Custom : ExcelCellValidator() {

        override fun isValid(target: Cell, block: (Cell.() -> String?)?): String? = block?.let { target.it() }
    }
}

fun ExcelWorkbook.validateCell(
    wb: Workbook,
    sheetName: String,
    rows: ClosedRange<Int>,
    cols: ClosedRange<String>,
    resultCol: String? = null
): Result<Workbook> = validate(ExcelValidateTarget.CELL, this, wb, sheetName, rows, cols, resultCol)

fun ExcelWorkbook.validateCol(
    wb: Workbook,
    sheetName: String,
    rows: ClosedRange<Int>,
    cols: ClosedRange<String>,
    resultCol: String? = null
): Result<Workbook> = validate(ExcelValidateTarget.COL, this, wb, sheetName, rows, cols, resultCol)

fun validate(
    target: ExcelValidateTarget,
    org: ExcelWorkbook,
    wb: Workbook,
    sheetName: String,
    rows: ClosedRange<Int>,
    cols: ClosedRange<String>,
    resultCol: String? = null
): Result<Workbook> {
    val validators: MutableMap<Any, MutableSet<ExcelCellValidator>> =
        org.sheets[sheetName]?.validators
            ?: throw IllegalStateException("시트가 없습니다. [Sheet=$sheetName]")
    var isValid: Boolean = true
    // 결과 Column 위치가 지정되지 않으면 마지막 Column 다음 Column 으로 설정한다.
    val resultColIndex = resultCol?.colIndex ?: (cols.endInclusive.colIndex + 1)
    val styles = ExcelStyleMap(wb)

    wb.getSheet(sheetName).run {
        val startRowIndex = rows.start.rowIndex
        // 마지막 Row 위치가 지정되지 않으면  검증 대상이 Cell이면 시작 Row 위치로 검증대상이 Column이면 데이터 마지막 Row 위치로 지정한다.
        var endRowIndex =
            if (rows.endInclusive < rows.start) startRowIndex
            else if (rows.endInclusive.rowIndex > lastRowNum) lastRowNum
            else rows.endInclusive.rowIndex
        if (endRowIndex < startRowIndex) endRowIndex = startRowIndex

        (startRowIndex..endRowIndex).forEach { rowIndex ->
            (cols.start.colIndex..cols.endInclusive.colIndex).forEach { colIndex ->
                cellAtIndex(rowIndex, colIndex)?.apply {
                    isValid =
                        validators[if (target == ExcelValidateTarget.CELL) address else address.column]
                        ?.firstNotNullOfOrNull { validator -> validator.isValid(this) }
                        ?.let { error ->
                            cellStyle = styles.style(ExcelStyle.ERROR)
                            createCellAtIndex(rowIndex, resultColIndex)?.apply {
                                this.cellStyle = styles.style(ExcelStyle.ERROR)
                                this.setCellValue(error)
                            }
                            false
                        } != false
                }
            }
        }
    }
    return if (isValid) Result.success(wb)
    else Result.failure(IllegalStateException("엑셀파일 파싱 중 오류가 발생하였습니다. [Sheet=$sheetName]"))
}

enum class ExcelValidateTarget {
    CELL,
    COL
}
