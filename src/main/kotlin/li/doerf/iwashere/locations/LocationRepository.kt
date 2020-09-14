package li.doerf.iwashere.locations

import li.doerf.iwashere.accounts.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface LocationRepository : JpaRepository<Location, Long> {
    fun countFirstByShortname(shortname: String): Int
    fun findFirstByShortname(shortname: String): Optional<Location>
    fun getAllByUser(user: User): List<Location>
}