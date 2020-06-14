package li.doerf.iwashere.controllers

import li.doerf.iwashere.dto.location.LocationCreateRequest
import li.doerf.iwashere.dto.location.LocationCreateResponse
import li.doerf.iwashere.dto.location.LocationListResponse
import li.doerf.iwashere.dto.location.toLocationDto
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.services.LocationsService
import li.doerf.iwashere.utils.getUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.transaction.Transactional
import kotlin.streams.toList

@RestController
@RequestMapping("locations")
@Transactional
class LocationsController(
        private val locationsService: LocationsService
) {

    @GetMapping
    fun list(principal: Principal): ResponseEntity<LocationListResponse> {
        val locations = locationsService.getAll(getUser(principal))
                .stream().map { it.toLocationDto() }.toList()
        return ok(LocationListResponse(locations))
    }

    @PostMapping
    fun create(request: LocationCreateRequest, principal: Principal): ResponseEntity<LocationCreateResponse> {
        val toBeCreated = Location(null, request.name, request.shortname, request.street, request.zip, request.city, request.country, getUser(principal))
        val loc = locationsService.create(toBeCreated, getUser(principal))
        return ok(LocationCreateResponse(loc.toLocationDto()))
    }

    @GetMapping("exists/{shortname}")
    fun exists(@PathVariable shortname: String, principal: Principal): HttpStatus {
        return if (locationsService.exists(shortname, getUser(principal))) {
            HttpStatus.OK
        } else {
            HttpStatus.NOT_FOUND
        }
    }

}