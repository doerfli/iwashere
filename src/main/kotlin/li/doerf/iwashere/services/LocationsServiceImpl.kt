package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl(
        private val locationRepository: LocationRepository
) : LocationsService {
    private val logger = getLogger(javaClass)

    override fun create(newLocation: Location, user: User): Location {
        logger.trace("create new location")
        if (newLocation.id != null) {
            throw IllegalArgumentException("location is already stored $newLocation")
        }
        if (newLocation.user != user) {
            throw IllegalArgumentException("user referenced in location does not match current user: $newLocation")
        }
        if (exists(newLocation.shortname, user)) {
            throw IllegalArgumentException("location with shortname already exists: $newLocation")
        }

        val loc = locationRepository.save(newLocation)
        logger.debug("location stored: $loc")
        return loc
    }

    override fun exists(shortname: String, user: User): Boolean {
        logger.trace("location exists: $shortname")
        val count = locationRepository.countFirstByShortnameAndUser(shortname, user)
        logger.debug("location $shortname count = $count")
        return count > 0
    }

    override fun getAll(user: User): List<Location> {
        logger.trace("get all locations")
        return locationRepository.getAllByUser(user)
    }
}
