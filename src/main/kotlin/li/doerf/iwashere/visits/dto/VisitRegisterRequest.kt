package li.doerf.iwashere.visits.dto

data class VisitRegisterRequest(
        val locationShortname: String,
        val name: String,
        val email: String,
        val phone: String,
        val timestamp: String? = null,
        val tableNumber: String? = null,
        val sector: String? = null,
)
