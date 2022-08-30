package wfm.shared.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.springframework.stereotype.Component

@Component
class TemplateSampleExcelService() {

    fun create(): ExcelWorkbook {
        return "엑셀파일명".workbook {
            style1()
            sheet1()
        }
    }
}

fun ExcelWorkbook.style1() {
    styles {
        "customStyle1" += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.RED.index
        }
        "customStyle2" += {
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.BLUE.index
        }
    }
}

fun ExcelWorkbook.sheet1(): ExcelWorkbook.ExcelSheet {
    return "Sheet1".sheet {
        1.row {
            init {
                ExcelStyle.HEADER_REQUIRED.style()
            }
            cols {
                "A".col("FC") {
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
                "B".col("FC ID") {
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
                "C".col("날짜") {
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
                "D".col("시간대") {
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
                "E".col("주문수 비율") {
                    15.width()
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
                "F".col("입고량 비율") {
                    15.width()
                    ExcelStyle.HEADER_OPTIONAL.style()
                    ExcelCellValidator.Equal(cell).cellValidator()
                }
            }
        }

        2.row {
            init {
                "customStyle1".style()
            }
            cols {
                "A".col {
                    ExcelCellType.STRING.type()
                    ExcelCellValidator.Required.colValidator()
                    ExcelCellValidator.Length(min = 1, max = 50).colValidator()
                }
                "B".col {
                    ExcelCellType.LONG.type()
                    ExcelCellValidator.Required.colValidator()
                }
                "C".col {
                    ExcelCellType.LOCALDATE.type()
                    ExcelCellValidator.Required.colValidator()
                }
                "D".col {
                    ExcelCellType.INTEGER.type()
                    ExcelCellValidator.Required.colValidator()
                    ExcelCellValidator.Range(min = 6, max = 23).colValidator()
                }
                "E".col {
                    ExcelCellType.DOUBLE.type()
                    ExcelCellValidator.Required.colValidator()
                    ExcelCellValidator.Range(min = 0).colValidator()
                    ExcelStyle.DATA.style()
                }
                "F".col {
                    ExcelCellType.DOUBLE.type()
                    ExcelCellValidator.Required.colValidator()
                    ExcelCellValidator.Range(min = 0).colValidator()
                    "customStyle2".style()
                }
            }
        }
    }
}
