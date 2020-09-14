package li.doerf.iwashere.accounts.dto

data class ChangePasswordRequest(
        val oldPassword: String,
        val newPassword: String
)