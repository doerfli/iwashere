package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface VisitRepository : PagingAndSortingRepository<Visit, Long> {

    fun findAllByRegistrationDateBefore(date: LocalDateTime): Collection<Visit>
    fun findAllByLocationAndRegistrationDateBetween(location: Location, after: LocalDateTime, before: LocalDateTime): Collection<Visit>

}