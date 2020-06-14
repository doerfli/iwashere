package li.doerf.iwashere.services.mail

import li.doerf.iwashere.entities.User

interface MailService {
    suspend fun sendSignupMail(user: User)
}