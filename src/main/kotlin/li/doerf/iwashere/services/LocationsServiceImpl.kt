package li.doerf.iwashere.services

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class LocationsServiceImpl(
        private val locationRepository: LocationRepository
) : LocationsService {
    private val logger = getLogger(javaClass)

    @Cacheable("location_exists")
    override fun exists(shortname: String, user: User): Boolean {
        logger.trace("location exists: $shortname")
        val count = locationRepository.countFirstByShortname(shortname)
        logger.debug("location $shortname count = $count")
        return count > 0
    }

    @Cacheable("location_shortname", key="#shortname")
    override fun getByShortName(shortname: String): Optional<Location> {
        logger.trace("get location with shortname $shortname")
        return locationRepository.findFirstByShortname(shortname)
    }

    @Cacheable("location_shortname_user", key="#shortname")
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

}
