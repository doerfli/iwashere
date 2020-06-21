package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Visit
import org.springframework.data.repository.PagingAndSortingRepository

interface VisitRepository : PagingAndSortingRepository<Visit, Long> {
}