package li.doerf.iwashere.controllers

import com.opencsv.CSVWriter
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.visit.*
import li.doerf.iwashere.services.LocationsService
import li.doerf.iwashere.services.VisitService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.getLogger
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("visits")
@Transactional
class VisitsController(
        private val visitService: VisitService,
        private val locationsService: LocationsService,
        private val userHelper: UserHelper
) {

    private val logger = getLogger(javaClass)

    @PostMapping
    fun register(@RequestBody request: VisitRegisterRequest) : ResponseEntity<VisitRegisterResponse> {
        logger.debug("registering visit $request")
        val visit = runBlocking { visitService.register(request.name, request.email, request.phone, request.locationShortname, request.timestamp) }
        return ok(VisitRegisterResponse(visit.id!!, visit.visitTimestamp))
    }

    @PutMapping("{id}/verify/email")
    fun verifyEmail(@PathVariable id: Long) : HttpStatus {
        logger.debug("confirm email visit $id")
        visitService.verifyEmail(id)
        return HttpStatus.OK
    }

    @PutMapping("{id}/verify/phone")
    fun verifyPhone(@PathVariable id: Long) : HttpStatus {
        logger.debug("confirm phone visit $id")
        visitService.verifyPhone(id)
        return HttpStatus.OK
    }

    @GetMapping("{shortname}/{date}")
    fun list(@PathVariable("shortname") locationShortname: String, @PathVariable("date") dateStr: String, principal: Principal): ResponseEntity<VisitListResponse> {
        logger.debug("retrieving visits for $locationShortname on $dateStr")
        val date = LocalDate.parse(dateStr)
        val guests = visitService.list(locationShortname, date, userHelper.getUser(principal))
                .stream().map { it.toDto() }.collect(Collectors.toList())
        return ok(VisitListResponse(guests))
    }

    @GetMapping("{shortname}/{date}/csv")
    fun listCsv(@PathVariable("shortname") locationShortname: String, @PathVariable("date") dateStr: String, principal: Principal, response: HttpServletResponse) {
        logger.debug("retrieving visits for $locationShortname on $dateStr")
        val date = LocalDate.parse(dateStr)
        val guests = visitService.list(locationShortname, date, userHelper.getUser(principal))
        val location = locationsService.getByShortName(locationShortname).get()

        logger.debug("preparing response (content type, filename)")
        response.contentType = "text/csv"
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${locationShortname}_${dateStr}.csv\"")

        logger.debug("writing csv")
        val csvWriter = CSVWriter(response.writer)
        csvWriter.writeNext(arrayOf("${location.name}, $dateStr"))
        csvWriter.writeNext(arrayOf("${location.street}, ${location.zip}, ${location.city}, ${location.country}"))
        csvWriter.writeNext(arrayOf())
        csvWriter.writeNext(arrayOf("name", "email", "verified", "phone", "verified"))
        guests.stream().forEach { csvWriter.writeNext(it.toCSV()) }
        csvWriter.close();
        logger.debug("writing csv finished")
    }

    @GetMapping("{shortname}/{date}/xlsx")
    fun listXlsx(@PathVariable("shortname") locationShortname: String, @PathVariable("date") dateStr: String, principal: Principal, response: HttpServletResponse) {
        logger.debug("retrieving visits for $locationShortname on $dateStr")
        val date = LocalDate.parse(dateStr)
        val guests = visitService.list(locationShortname, date, userHelper.getUser(principal))
        val location = locationsService.getByShortName(locationShortname).get()

        logger.debug("preparing response (content type, filename)")
        response.contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM.type
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${locationShortname}_${dateStr}.xlsx\"")

        logger.debug("creating xls")

        var row = 0

        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Persons")
        val header1 = sheet.createRow(row++)
        header1.createCell(0).setCellValue("${location.name}, $dateStr")
        val header2 = sheet.createRow(row++)
        header2.createCell(0).setCellValue("${location.street}, ${location.zip}, ${location.city}, ${location.country}")
        row++

        val header3 = sheet.createRow(row++)
        header3.createCell(0).setCellValue("name")
        header3.createCell(1).setCellValue("email")
        header3.createCell(2).setCellValue("phone")

        guests.stream().forEach {
            val rw = sheet.createRow(row++)
            rw.createCell(0).setCellValue(it.guest.name)
            rw.createCell(1).setCellValue(it.guest.email)
            rw.createCell(2).setCellValue(it.guest.phone)
        }

        logger.debug("writing xls")
        workbook.write(response.outputStream)
        workbook.close()
        logger.debug("writing xls finished")
    }

    @GetMapping("{shortname}/dates")
    fun getDatesWithVisits(@PathVariable("shortname") locationShortname: String, principal: Principal): ResponseEntity<VisitListDatesReponse> {
        logger.debug("retrieving dates with visitors for $locationShortname")
        val dates = visitService.listDates(locationShortname, userHelper.getUser(principal))
                .map { DateGuestcountDto(it.key, it.value) }
        return ok(VisitListDatesReponse(dates))
    }

}