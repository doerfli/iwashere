package li.doerf.iwashere.dto.account

data class ResetPasswordRequest(
        val token: String,
        val password: String
)