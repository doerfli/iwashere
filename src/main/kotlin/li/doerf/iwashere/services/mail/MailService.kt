package li.doerf.iwashere.services.mail

import li.doerf.iwashere.documents.User

interface MailService {
    suspend fun sendSignupMail(user: User)
}