package wfm.shared.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/sample")
class SampleExcelController(
    private val sampleDownloadExcelService: SampleDownloadExcelService,
    private val templateSampleExcelService: TemplateSampleExcelService,
    private val sampleUploadExcelService: SampleUploadExcelService
) {
    @GetMapping("/excel")
    fun downloadSample(response: HttpServletResponse) {
        sampleDownloadExcelService
            .execute()
            .download(response)
    }

    @GetMapping("/excel/template")
    fun downloadCreateForm(response: HttpServletResponse) {
        templateSampleExcelService
            .create()
            .download(response)
    }

    @PostMapping("/excel/upload")
    fun uploadCreateForm(
        @RequestPart(name = "excelFile", required = true) file: MultipartFile,
        response: HttpServletResponse
    ) {
        XSSFWorkbook(ByteArrayInputStream(file.bytes))
            .run {
                val result = sampleUploadExcelService.upload(this)
                if (result.isFailure) download(response, "엑셀파일명")
            }
    }
}
