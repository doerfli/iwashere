package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.Instant

interface VisitRepository : PagingAndSortingRepository<Visit, Long> {

    fun findAllByRegistrationDateBefore(date: Instant): Collection<Visit>
    fun findAllByLocationAndRegistrationDateBetween(location: Location, after: Instant, before: Instant): Collection<Visit>

}