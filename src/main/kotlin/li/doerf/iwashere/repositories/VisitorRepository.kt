package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Visitor
import org.springframework.data.repository.PagingAndSortingRepository

interface VisitorRepository : PagingAndSortingRepository<Visitor, Long> {
}