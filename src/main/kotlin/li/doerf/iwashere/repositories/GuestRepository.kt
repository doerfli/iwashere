package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Guest
import org.springframework.data.repository.PagingAndSortingRepository

interface GuestRepository : PagingAndSortingRepository<Guest, Long> {

}