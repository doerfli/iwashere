package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User

interface LocationsService {
    fun create(newLocation: Location, user: User): Location
    fun exists(shortname: String, user: User): Boolean
    fun getAll(user: User): List<Location>
}