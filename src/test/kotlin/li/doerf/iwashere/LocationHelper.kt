package li.doerf.iwashere

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User

class LocationHelper {

    companion object {
        fun new(name: String, shortname: String, user: User): Location {
            return Location(null, name, shortname, null, null, null, null, user)
        }
    }

}