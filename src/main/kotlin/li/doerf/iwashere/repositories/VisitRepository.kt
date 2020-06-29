package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface VisitRepository : PagingAndSortingRepository<Visit, Long> {

    fun findAllByRegistrationDateBefore(date: LocalDateTime): Collection<Visit>
    fun findAllByLocationAndRegistrationDateBetween(location: Location, after: LocalDateTime, before: LocalDateTime): Collection<Visit>
    @Query("""
        SELECT DISTINCT 
            concat(
                function('year', registrationDate),
                '-',
                function('month', registrationDate),
                '-',
                function('day', registrationDate)
            ) as date,
            count(id) as guestcount
        FROM Visit 
        WHERE 
            location=?1 
        GROUP BY 
            date
    """)
    fun getDateGuestCountList(location: Location): List<DateGuestcount>

}