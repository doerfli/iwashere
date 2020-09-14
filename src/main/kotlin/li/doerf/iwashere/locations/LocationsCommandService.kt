package li.doerf.iwashere.locations

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.locations.dto.LocationDto
import li.doerf.iwashere.utils.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class LocationsCommandService(
        private val locationsService: LocationsService,
        private val locationRepository: LocationRepository) {

    private val logger = getLogger(this::class.java)

    fun create(newLocation: Location, user: User): Location {
        logger.trace("create new location")
        if (newLocation.id != null) {
            throw IllegalArgumentException("location is already stored $newLocation")
        }
        if (newLocation.user != user) {
            throw IllegalArgumentException("user referenced in location does not match current user: $newLocation")
        }
        if (locationsService.exists(newLocation.shortname, user)) {
            throw IllegalArgumentException("location with shortname already exists: $newLocation")
        }

        val loc = locationRepository.save(newLocation)
        logger.info("location stored: $loc")
        return loc
    }

    /** Update location properties except id and shortname */
    @Caching(evict = [
        CacheEvict("location_shortname", key="#entity.shortname"),
        CacheEvict("location_shortname_user", key="#entity.shortname")])
    fun update(entity: LocationDto, user: User): Location {
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

    fun updateShortname(id: Long, shortname: String, user: User): Location {
        val loc = getLocationForUser(id, user)
        if (locationsService.exists(shortname, user)) {
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