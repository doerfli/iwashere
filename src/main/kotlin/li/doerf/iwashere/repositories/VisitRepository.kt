package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Visit
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.Instant

interface VisitRepository : PagingAndSortingRepository<Visit, Long> {

    fun findAllByRegistrationDateBefore(date: Instant): Collection<Visit>

}