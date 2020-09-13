package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface VisitRepository : JpaRepository<Visit, Long> {

    fun findAllByVisitTimestampBefore(date: LocalDateTime): Collection<Visit>
    fun findAllByLocation(location: Location): Collection<Visit>
    fun findAllByLocationAndVisitTimestampBetween(location: Location, after: LocalDateTime, before: LocalDateTime): Collection<Visit>
    fun countAllByVisitTimestampBetween(after: LocalDateTime, before: LocalDateTime): Long
    @Query("""
        SELECT DISTINCT 
            concat(
                function('year', visitTimestamp),
                '-',
                function('month', visitTimestamp),
                '-',
                function('day', visitTimestamp)
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