package wfm.shared.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook

class ExcelStyleMap(private val workbook: Workbook) {
    private val stylesMap: MutableMap<String, CellStyle> = mutableMapOf()

    init {
        ExcelStyle.ERROR.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.DARK_RED.index
            setFont(
                workbook.createFont().apply {
                    color = IndexedColors.WHITE.index
                }
            )
        }

        ExcelStyle.HEADER.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.SKY_BLUE.index
            setFont(
                workbook.createFont().apply {
                    bold = true
                }
            )
            locked = true
        }

        ExcelStyle.HEADER_REQUIRED.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.BRIGHT_GREEN.index
            setFont(
                workbook.createFont().apply {
                    bold = true
                }
            )
            locked = true
        }

        ExcelStyle.HEADER_OPTIONAL.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            setFont(
                workbook.createFont().apply {
                    bold = true
                }
            )
            locked = true
        }

        ExcelStyle.DATA.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
        }

        ExcelStyle.DATA_CENTER.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            alignment = HorizontalAlignment.CENTER
        }

        ExcelStyle.DATA_READONLY.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
            locked = true
        }

        ExcelStyle.DATA_NUMERIC.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            dataFormat = "#,##0".format()
        }

        ExcelStyle.DATA_PRICE.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            dataFormat = "#,##0.00".format()
        }

        ExcelStyle.DATA_RATE.name += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            dataFormat = "#,##0.####".format()
        }
    }

    operator fun String.plusAssign(block: CellStyle.() -> Unit): Unit =
        stylesMap
            .getOrPut(this) {
                workbook.createCellStyle()
            }
            .block()

    private fun String.format(): Short =
        workbook.createDataFormat().getFormat(this)

    // Cell Style Fetch
    fun style(styleName: String): CellStyle? = stylesMap[styleName]
    fun style(style: ExcelStyle): CellStyle? = stylesMap[style.name]
}

enum class ExcelStyle {
    ERROR,
    HEADER,
    HEADER_REQUIRED,
    HEADER_OPTIONAL,
    DATA,
    DATA_CENTER,
    DATA_READONLY,
    DATA_NUMERIC,
    DATA_PRICE,
    DATA_RATE
}
