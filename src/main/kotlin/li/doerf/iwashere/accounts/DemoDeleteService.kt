package li.doerf.iwashere.accounts

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.repositories.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class DemoDeleteService @Autowired constructor(
        private val locationRepository: LocationRepository
) {
//    private val log = getLogger(this::class.java)

    @Caching(evict = [
        CacheEvict("location_shortname", key="#entity.shortname"),
        CacheEvict("location_shortname_user", key="#entity.shortname")])
    open fun deleteLocation(entity: Location) {
        locationRepository.delete(entity)
    }


}