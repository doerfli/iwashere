package li.doerf.iwashere.controllers

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.visit.VisitListResponse
import li.doerf.iwashere.dto.visit.VisitRegisterRequest
import li.doerf.iwashere.dto.visit.VisitRegisterResponse
import li.doerf.iwashere.dto.visit.toDto
import li.doerf.iwashere.services.VisitService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate
import java.util.stream.Collectors

@RestController
@RequestMapping("visits")
@Transactional
class VisitsController(
        private val visitService: VisitService,
        private val userHelper: UserHelper
) {

    private val logger = getLogger(javaClass)

    @PostMapping
    fun register(@RequestBody request: VisitRegisterRequest) : ResponseEntity<VisitRegisterResponse> {
        logger.debug("registering visit $request")
        val visit = runBlocking { visitService.register(request.name, request.email, request.phone, request.locationShortname) }
        return ok(VisitRegisterResponse(visit.id!!, visit.registrationDate))
    }

    @GetMapping("{shortname}/{date}")
    fun list(@PathVariable("shortname") locationShortname: String, @PathVariable("date") dateStr: String, principal: Principal): ResponseEntity<VisitListResponse> {
        logger.debug("retrieving visits for $locationShortname on $dateStr")
        val date = LocalDate.parse(dateStr)
        val guests = visitService.list(locationShortname, date, userHelper.getUser(principal))
                .stream().map { it.toDto() }.collect(Collectors.toList())
        return ok(VisitListResponse(guests))
    }

}