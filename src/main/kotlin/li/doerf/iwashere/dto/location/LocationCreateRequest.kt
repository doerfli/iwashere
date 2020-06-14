package li.doerf.iwashere.dto.location

data class LocationCreateRequest(
        val name: String,
        var shortname: String,
        var street: String?,
        var zip: String?,
        var city: String?,
        var country: String?
)
