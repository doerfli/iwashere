package li.doerf.iwashere.locations.dto

import li.doerf.iwashere.locations.Location

data class LocationDto(
        val id: Long,
        val name: String,
        val shortname: String,
        val street: String?,
        val zip: String?,
        val city: String?,
        val country: String?
)

fun Location.toLocationDto(): LocationDto {
    return LocationDto(
            this.id!!,
            this.name,
            this.shortname,
            this.street,
            this.zip,
            this.city,
            this.country
    )
}
