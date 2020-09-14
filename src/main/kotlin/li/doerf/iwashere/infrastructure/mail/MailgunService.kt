package li.doerf.iwashere.infrastructure.mail

interface MailgunService {
    suspend fun sendEmail(sender: String, recipient: String, subject: String, text: String)
}