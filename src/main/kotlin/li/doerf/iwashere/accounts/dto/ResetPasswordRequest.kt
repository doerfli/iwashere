package li.doerf.iwashere.accounts.dto

data class ResetPasswordRequest(
        val token: String,
        val password: String
)