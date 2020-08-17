package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import java.time.LocalDate

interface VisitService {
    suspend fun register(name: String, email: String, phone: String, locationShortname: String, timestamp: String? = null): Visit
    fun cleanup(retentionDays: Long): Any
    fun list(locationShortname: String, date: LocalDate, user: User): Collection<Visit>
    fun listDates(locationShortname: String, user: User): Map<String, Long>
    fun countToday(): Long
    fun verifyEmail(id: Long)
    fun verifyPhone(id: Long)
}
