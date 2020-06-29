package li.doerf.iwashere.dto.visit

data class VisitRegisterRequest(
        val locationShortname: String,
        val name: String,
        val email: String,
        val phone: String,
        val timestamp: String? = null
)
