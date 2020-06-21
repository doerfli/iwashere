package li.doerf.iwashere.services

import li.doerf.iwashere.dto.location.LocationDto
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import java.util.*

interface LocationsService {
    fun create(newLocation: Location, user: User): Location
    fun exists(shortname: String, user: User): Boolean
    fun getByShortName(shortname: String): Optional<Location>
    fun getAll(user: User): List<Location>
    /** Update location properties except id and shortname */
    fun update(entity: LocationDto, user: User): Location
    fun updateShortname(id: Long, shortname: String, user: User): Location
}