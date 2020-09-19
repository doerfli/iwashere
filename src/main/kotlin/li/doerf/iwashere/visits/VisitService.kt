package li.doerf.iwashere.visits

import li.doerf.iwashere.accounts.User
import java.time.LocalDate

interface VisitService {
    suspend fun register(name: String, email: String, phone: String, locationShortname: String, timestampStr: String? = null, tableNumber: String?, sector: String?, suppressEmail: Boolean = false): Visit
    fun cleanup(retentionDays: Long): Any
    fun getAllByLocation(locationShortname: String, user: User): Collection<Visit>
    fun list(locationShortname: String, date: LocalDate, user: User): Collection<Visit>
    fun listDates(locationShortname: String, user: User): Map<String, Long>
    fun countToday(): Long
    fun verifyEmail(id: Long)
    fun verifyPhone(id: Long)
}
