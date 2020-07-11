package li.doerf.iwashere.services.mail

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit

interface MailService {
    suspend fun sendSignupMail(user: User)
    suspend fun sendVisitMail(visit: Visit)
    suspend fun sendResetPasswordMail(user: User)
}