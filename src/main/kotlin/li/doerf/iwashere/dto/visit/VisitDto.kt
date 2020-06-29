package li.doerf.iwashere.dto.visit

import li.doerf.iwashere.entities.Visit
import java.text.SimpleDateFormat
import java.util.*

data class VisitDto(
        val id: Long,
        val guest_name: String,
        val guest_email: String?,
        val guest_phone: String?,
        val date: String
)

fun Visit.toDto(): VisitDto {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return VisitDto(
            this.id!!,
            this.guest.name,
            this.guest.email,
            this.guest.phone,
            formatter.format(Date.from(this.registrationDate))
    )
}