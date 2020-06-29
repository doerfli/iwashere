package li.doerf.iwashere.services

import li.doerf.iwashere.dto.location.LocationDto
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service
import java.util.*

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
        logger.info("location stored: $loc")
        return loc
    }

    override fun exists(shortname: String, user: User): Boolean {
        logger.trace("location exists: $shortname")
        val count = locationRepository.countFirstByShortname(shortname)
        logger.debug("location $shortname count = $count")
        return count > 0
    }

    override fun getByShortName(shortname: String): Optional<Location> {
        logger.trace("get location with shortname $shortname")
        return locationRepository.findFirstByShortname(shortname)
    }

    override fun getByShortName(shortname: String, user: User): Optional<Location> {
        logger.trace("get location with shortname $shortname")
        val location = locationRepository.findFirstByShortname(shortname)
        if (location.isPresent) {
            if (location.get().user.id != user.id) {
                throw IllegalArgumentException("user is not allowed to access location")
            }
        }
        return location
    }

    override fun getAll(user: User): List<Location> {
        logger.trace("get all locations")
        return locationRepository.getAllByUser(user)
    }

    override fun update(entity: LocationDto, user: User): Location {
        logger.trace("location update request: $entity")
        val loc = getLocationForUser(entity.id, user)

        val updatedLoc = loc.copy(
                name = entity.name,
                street = entity.street,
                zip = entity.zip,
                city = entity.city,
                country = entity.country
        )
        val result = locationRepository.save(updatedLoc)
        logger.info("updated location $result")
        return result
    }

    override fun updateShortname(id: Long, shortname: String, user: User): Location {
        val loc = getLocationForUser(id, user)
        if (exists(shortname, user)) {
            throw java.lang.IllegalArgumentException("shortname already in use: $shortname")
        }

        val updatedLoc = loc.copy(
                shortname = shortname
        )
        val result = locationRepository.save(updatedLoc)
        logger.info("updated location shortname $result")
        return result
    }

    private fun getLocationForUser(id: Long, user: User): Location {
        val locOpt = locationRepository.findById(id)
        if (locOpt.isEmpty) {
            throw IllegalArgumentException("location with id does not exist: ${id}")
        }
        val loc = locOpt.get()
        if (loc.user.id != user.id) {
            throw IllegalArgumentException("user is not allowed to access location")
        }
        return loc
    }
}
