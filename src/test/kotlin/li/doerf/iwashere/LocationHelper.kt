package li.doerf.iwashere

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.entities.Location

class LocationHelper {

    companion object {
        fun new(name: String, shortname: String, user: User): Location {
            return Location(null, name, shortname, null, null, null, null, user = user)
        }
    }

}