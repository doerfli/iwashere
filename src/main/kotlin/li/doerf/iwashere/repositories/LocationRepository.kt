package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import org.springframework.data.repository.PagingAndSortingRepository


interface LocationRepository : PagingAndSortingRepository<Location, Long> {
    fun countFirstByShortnameAndUser(shortname: String, user: User): Int
    fun getAllByUser(user: User): List<Location>
}