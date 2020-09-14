package li.doerf.iwashere.visits.dto

import li.doerf.iwashere.visits.Visit
import java.time.format.DateTimeFormatter

data class VisitDto(
        val id: Long,
        val guest_name: String,
        val guest_email: String?,
        val guest_phone: String?,
        val visitTimestamp: String,
        val verifiedEmail: Boolean,
        val verifiedPhone: Boolean
)

val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun Visit.toDto(): VisitDto {
    return VisitDto(
            this.id!!,
            this.guest.name,
            this.guest.email,
            this.guest.phone,
            fmt.format(this.visitTimestamp),
            this.verifiedEmail,
            this.verifiedPhone
    )
}

fun Visit.toCSV(): Array<String> {
    return arrayOf(
            this.guest.name,
            this.guest.email,
            if (this.verifiedEmail) { "y" } else { "n" },
            this.guest.phone,
            if (this.verifiedPhone) { "y" } else { "n" },
    )
}