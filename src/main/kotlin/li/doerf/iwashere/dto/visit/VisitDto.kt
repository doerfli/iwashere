package li.doerf.iwashere.dto.visit

import li.doerf.iwashere.entities.Visit
import java.time.format.DateTimeFormatter

data class VisitDto(
        val id: Long,
        val guest_name: String,
        val guest_email: String?,
        val guest_phone: String?,
        val visitTimestamp: String
)

val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun Visit.toDto(): VisitDto {
    return VisitDto(
            this.id!!,
            this.guest.name,
            this.guest.email,
            this.guest.phone,
            fmt.format(this.visitTimestamp)
    )
}