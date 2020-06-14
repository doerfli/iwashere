package li.doerf.iwashere.controllers

import li.doerf.iwashere.dto.location.LocationCreateRequest
import li.doerf.iwashere.dto.location.LocationCreateResponse
import li.doerf.iwashere.dto.location.LocationListResponse
import li.doerf.iwashere.dto.location.toLocationDto
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.services.LocationsService
import li.doerf.iwashere.utils.UserHelper
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.transaction.Transactional
import kotlin.streams.toList

@RestController
@RequestMapping("locations")
@Transactional
class LocationsController(
        private val locationsService: LocationsService,
        private val userHelper: UserHelper
) {

    @GetMapping
    fun list(principal: Principal): ResponseEntity<LocationListResponse> {
        val locations = locationsService.getAll(userHelper.getUser(principal))
                .stream().map { it.toLocationDto() }.toList()
        return ok(LocationListResponse(locations))
    }

    @PostMapping
    fun create(request: LocationCreateRequest, principal: Principal): ResponseEntity<LocationCreateResponse> {
        val toBeCreated = Location(null, request.name, request.shortname, request.street, request.zip, request.city, request.country, userHelper.getUser(principal))
        val loc = locationsService.create(toBeCreated, userHelper.getUser(principal))
        return ok(LocationCreateResponse(loc.toLocationDto()))
    }

    @GetMapping("exists/{shortname}")
    fun exists(@PathVariable shortname: String, principal: Principal): ResponseEntity<Void> {
        return if (locationsService.exists(shortname, userHelper.getUser(principal))) {
            ok().build()
        } else {
            notFound().build()
        }
    }

}