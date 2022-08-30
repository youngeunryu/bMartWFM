package wfm.shared.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SampleDownloadExcelService() {

    fun execute(): ExcelWorkbook {
        return "엑셀파일명".workbook {
            style1()
            sheet1(getDataList())
        }
    }

    private fun ExcelWorkbook.style1() {
        styles {
            "customStyle1" += {
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                borderBottom = BorderStyle.THIN
                fillPattern = FillPatternType.SOLID_FOREGROUND
                fillForegroundColor = IndexedColors.RED.index
            }
        }
    }

    private fun ExcelWorkbook.sheet1(list: List<SampleData>): ExcelWorkbook.ExcelSheet {
        return "Sheet1".sheet {
            1.row {
                ExcelStyle.HEADER.style()
                20.height()
                cols {
                    "A".col("FC") {
                        20.width()
                    }
                    "B".col("FC ID")
                    "C".col("날짜") {
                        12.width()
                    }
                    "D".col("시간대")
                    "E".col("주문수 비율")
                    "F".col("입고량 비율")
                    "G".col("비고") {
                        hide()
                    }
                }
            }

            2.rows(list) {
                ExcelStyle.DATA.style()
                cols { item ->
                    "A".col(item.fc)
                    "B".col(item.fcId)
                    "C".col(item.date) {
                        ExcelStyle.DATA_CENTER.style()
                    }
                    "D".col(item.hour)
                    "E".col(item.orderQuantityRate) {
                        ExcelStyle.DATA_RATE.style()
                    }
                    "F".col(item.inboundQuantityRate) {
                        ExcelStyle.DATA_RATE.style()
                    }
                }
            }
        }
    }

    private fun getDataList(): List<SampleData> =
        listOf(
            SampleData(
                fc = "강남센터",
                fcId = 1,
                date = LocalDate.now(),
                hour = 6,
                orderQuantityRate = 1.3,
                inboundQuantityRate = 2.5
            ),
            SampleData(
                fc = "강남센터",
                fcId = 1,
                date = LocalDate.now(),
                hour = 7,
                orderQuantityRate = 1.2,
                inboundQuantityRate = 2.4
            ),
            SampleData(
                fc = "강남센터",
                fcId = 1,
                date = LocalDate.now(),
                hour = 8,
                orderQuantityRate = 1.4,
                inboundQuantityRate = 2.2
            ),
            SampleData(
                fc = "강남센터",
                fcId = 1,
                date = LocalDate.now(),
                hour = 9,
                orderQuantityRate = 1.5,
                inboundQuantityRate = 2.1
            ),
            SampleData(
                fc = "강북센터",
                fcId = 2,
                date = LocalDate.now(),
                hour = 6,
                orderQuantityRate = 1.0,
                inboundQuantityRate = 1.1
            )
        )
}
