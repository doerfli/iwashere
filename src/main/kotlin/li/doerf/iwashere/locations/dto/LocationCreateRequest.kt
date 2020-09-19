package li.doerf.iwashere.locations.dto

data class LocationCreateRequest(
        val name: String,
        var shortname: String,
        var street: String?,
        var zip: String?,
        var city: String?,
        var country: String?,
        var useTableNumber: Boolean,
        var useSector: Boolean
)
