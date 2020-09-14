package li.doerf.iwashere.locations

import li.doerf.iwashere.accounts.User
import java.util.*

interface LocationsService {
    fun exists(shortname: String, user: User): Boolean
    fun getByShortName(shortname: String, user: User): Optional<Location>
    fun getByShortName(shortname: String): Optional<Location>
    fun getAll(user: User): List<Location>
}