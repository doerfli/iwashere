package li.doerf.iwashere.services.mail

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.visits.Visit

interface MailService {
    suspend fun sendSignupMail(user: User)
    suspend fun sendVisitMail(visit: Visit)
    suspend fun sendForgotPasswordMail(user: User)
    suspend fun sendPasswordResetMail(user: User)
    suspend fun sendFeedback(name: String, email: String, message: String)
}