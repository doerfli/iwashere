package li.doerf.iwashere.dto.account

data class ChangePasswordRequest(
        val oldPassword: String,
        val newPassword: String
)