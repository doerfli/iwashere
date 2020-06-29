package li.doerf.iwashere.services.mail

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import io.github.cdimascio.dotenv.dotenv
import li.doerf.iwashere.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MailgunServiceImpl @Autowired constructor(
        val fuel: Fuel
) : MailgunService {
    private val logger = getLogger(javaClass)

    @Value("\${mailgun.apikey:1234567890}")
    private lateinit var apiKey: String
    @Value("\${mailgun.baseurl}")
    private lateinit var baseUrl: String

    override suspend fun sendEmail(sender: String, recipient: String, subject: String, text: String) {
        var dontSendMails = dotenv {}["DONT_SEND_MAILS"]
        if (! dontSendMails.isNullOrEmpty() && dontSendMails.toBoolean()) {
            logger.debug("DONT_SEND_MAILS active - no sending of mails")
            return
        }

        val body= listOf(
                "from" to sender,
                "to" to recipient,
                "subject" to subject,
                "text" to text
        )

        val url = "$baseUrl/messages"
        logger.trace("posting to url $url")
        val response = sendRequest(url, body)
        logger.debug("request to send mail submitted - response statusCode ${response.statusCode}")
        if (response.statusCode != 200) {
            logger.error(response.statusCode.toString() + " " + response.responseMessage)
            throw MailNotSentException("request to send email not successful - recipient $recipient")
        }
    }

    // visible for testing
    suspend fun sendRequest(url: String, body: List<Pair<String, String>>): Response {
        return fuel.upload(url, Method.POST, body)
                .authentication()
                .basic("api", apiKey)
                .awaitStringResponseResult().second
    }

}