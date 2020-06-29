package li.doerf.iwashere.controllers

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.visit.VisitRegisterRequest
import li.doerf.iwashere.dto.visit.VisitRegisterResponse
import li.doerf.iwashere.services.VisitService
import li.doerf.iwashere.utils.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("visits")
@Transactional
class VisitController(
        private val visitService: VisitService
) {

    private val logger = getLogger(javaClass)

    @PostMapping
    fun register(@RequestBody request: VisitRegisterRequest) : ResponseEntity<VisitRegisterResponse> {
        logger.debug("registering visit $request")
        val visit = runBlocking { visitService.register(request.name, request.email, request.phone, request.locationShortname) }
        return ok(VisitRegisterResponse(visit.id!!, visit.registrationDate))
    }

}