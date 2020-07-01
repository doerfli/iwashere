package li.doerf.iwashere.controllers

import com.opencsv.CSVWriter
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.visit.*
import li.doerf.iwashere.services.LocationsService
import li.doerf.iwashere.services.VisitService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.annotation.Transactional
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
        csvWriter.writeNext(arrayOf("name", "email", "phone"))
        guests.stream().forEach { csvWriter.writeNext(it.toCSV()) }
        csvWriter.close();
        logger.debug("writing csv finished")
    }

    @GetMapping("{shortname}/dates")
    fun getDatesWithVisits(@PathVariable("shortname") locationShortname: String, principal: Principal): ResponseEntity<VisitListDatesReponse> {
        logger.debug("retrieving dates with visitors for $locationShortname")
        val dates = visitService.listDates(locationShortname, userHelper.getUser(principal))
                .map { DateGuestcountDto(it.key, it.value) }
        return ok(VisitListDatesReponse(dates))
    }

}