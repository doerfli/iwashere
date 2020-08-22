package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import java.util.*

interface LocationsService {
    fun exists(shortname: String, user: User): Boolean
    fun getByShortName(shortname: String, user: User): Optional<Location>
    fun getByShortName(shortname: String): Optional<Location>
    fun getAll(user: User): List<Location>
}