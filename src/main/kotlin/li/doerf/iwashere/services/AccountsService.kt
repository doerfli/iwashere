package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User

interface AccountsService {

    suspend fun create(username: String, password: String): User
    fun confirm(token: String): User
    fun changePassword(oldPassword: String, newPassword: String, username: String)
    suspend fun forgotPassword(username: String)

}