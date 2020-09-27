package li.doerf.iwashere.visits.dto

import li.doerf.iwashere.visits.Visit
import java.time.format.DateTimeFormatter

data class VisitDto(
        val id: Long,
        val guest_name: String,
        val guest_email: String?,
        val guest_phone: String?,
        val guest_street: String?,
        val guest_zip: String?,
        val guest_city: String?,
        val guest_country: String?,
        val visitTimestamp: String,
        val verifiedEmail: Boolean,
        val verifiedPhone: Boolean,
        val tableNumber: String?,
        val sector: String?,
)

val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun Visit.toDto(): VisitDto {
    return VisitDto(
            this.id!!,
            this.guest.name,
            this.guest.email,
            this.guest.phone,
            this.guest.street,
            this.guest.zip,
            this.guest.city,
            this.guest.country,
            fmt.format(this.visitTimestamp),
            this.verifiedEmail,
            this.verifiedPhone,
            this.tableNumber,
            this.sector,
    )
}

fun Visit.toCSV(): Array<String> {
    return arrayOf(
            this.guest.name,
            this.guest.email,
            if (this.verifiedEmail) { "y" } else { "n" },
            this.guest.phone,
            if (this.verifiedPhone) { "y" } else { "n" },
            this.guest.street.orEmpty(),
            this.guest.zip.orEmpty(),
            this.guest.city.orEmpty(),
            this.guest.country.orEmpty(),
            this.tableNumber.orEmpty(),
            this.sector.orEmpty(),
    )
}