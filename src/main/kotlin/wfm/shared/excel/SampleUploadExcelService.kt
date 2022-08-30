package wfm.shared.excel

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component

@Component
class SampleUploadExcelService {

    fun upload(wb: Workbook): Result<Workbook> =
        validate(wb)
            .onSuccess {
                it.getSheet("Sheet1")
                    .parse()
                    .save()
            }

    private fun validate(wb: Workbook): Result<Workbook> =
        workbook { sheet1() }
            .run {
                val result1 = validateCell(
                    wb = wb,
                    sheetName = "Sheet1",
                    rows = 1..1,
                    cols = "A".."F"
                )
                val result2 = validateCol(
                    wb = wb,
                    sheetName = "Sheet1",
                    rows = 2..Int.MAX_VALUE,
                    cols = "A".."F"
                )
                if (result1.isFailure) result1
                else if (result2.isFailure) result2
                else result1
            }

    private fun Sheet.parse(): List<SampleData> =
        (2..lastRowNumber).map { row ->
            SampleData(
                fc = cellAt(row, "A")?.stringCellValue!!,
                fcId = cellAt(row, "B")?.numericCellValue?.toLong()!!,
                date = cellAt(row, "C")?.localDateTimeCellValue?.toLocalDate()!!,
                hour = cellAt(row, "D")?.numericCellValue?.toInt()!!,
                orderQuantityRate = cellAt(row, "E")?.numericCellValue!!,
                inboundQuantityRate = cellAt(row, "F")?.numericCellValue!!
            )
        }

    private fun List<SampleData>.save() {
    }
}
