package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User

interface AccountsService {

    suspend fun create(username: String, password: String): User

}