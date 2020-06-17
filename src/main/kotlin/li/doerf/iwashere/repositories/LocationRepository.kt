package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import org.springframework.data.repository.PagingAndSortingRepository


interface LocationRepository : PagingAndSortingRepository<Location, Long> {
    fun countFirstByShortname(shortname: String): Int
    fun getAllByUser(user: User): List<Location>
}