package li.doerf.iwashere.services.mail

interface MailgunService {
    suspend fun sendEmail(sender: String, recipient: String, subject: String, text: String)
}