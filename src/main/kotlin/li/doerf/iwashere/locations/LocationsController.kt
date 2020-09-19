package li.doerf.iwashere.locations

import li.doerf.iwashere.locations.dto.*
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
        private val locationsCommandService: LocationsCommandService,
        private val userHelper: UserHelper
) {

    @GetMapping
    fun list(principal: Principal): ResponseEntity<LocationListResponse> {
        val locations = locationsService.getAll(userHelper.getUser(principal))
                .stream().map { it.toLocationDto() }.toList()
        return ok(LocationListResponse(locations))
    }

    @GetMapping("/byShortname/{shortname}")
    fun get(@PathVariable("shortname") shortname: String): ResponseEntity<LocationDto> {
        val locationOpt = locationsService.getByShortName(shortname)
        if (locationOpt.isEmpty) {
            throw IllegalArgumentException("Location with shortname '$shortname' does not exist")
        }
        return ok(locationOpt.get().toLocationDto())
    }

    @PostMapping
    fun create(@RequestBody request: LocationCreateRequest, principal: Principal): ResponseEntity<LocationCreateResponse> {
        val toBeCreated = Location(null, request.name, request.shortname, request.street, request.zip, request.city,
                request.country, user = userHelper.getUser(principal), useTableNumber = request.useTableNumber,
                useSector = request.useSector)
        val loc = locationsCommandService.create(toBeCreated, userHelper.getUser(principal))
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

    @PutMapping
    fun update(@RequestBody request: LocationUpdateRequest, principal: Principal): ResponseEntity<LocationUpdateResponse> {
        val updated = locationsCommandService.update(request.entity, userHelper.getUser(principal))
        return ok(LocationUpdateResponse(updated.toLocationDto()))
    }

    @PutMapping("updateShortname")
    fun updateShortname(@RequestBody request: LocationUpdateShortnameRequest, principal: Principal): ResponseEntity<LocationUpdateShortnameResponse> {
        val updated = locationsCommandService.updateShortname(request.id, request.shortname, userHelper.getUser(principal))
        return ok(LocationUpdateShortnameResponse(updated.id!!, updated.shortname))
    }

}