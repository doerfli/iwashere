package li.doerf.iwashere.dto.visit

data class VisitRegisterRequest(
        val locationShortname: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        val phone: String
)
