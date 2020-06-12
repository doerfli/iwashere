package li.doerf.iwashere.services.mail

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.reactor.monoResponse
import li.doerf.iwashere.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MailgunServiceImpl @Autowired constructor(
        val fuel: Fuel
) : MailgunService {
    private val logger = getLogger(javaClass)

    @Value("\${mailgun.apikey:1234567890}")
    private lateinit var apiKey: String
    @Value("\${mailgun.baseurl}")
    private lateinit var baseUrl: String

    override fun sendEmail(sender: String, recipient: String, subject: String, text: String): Mono<Boolean> {
        val body= listOf(
                "from" to sender,
                "to" to recipient,
                "subject" to subject,
                "text" to text
        )

        val url = "$baseUrl/messages"
        logger.trace("posting to url $url")
        val responseMono = sendRequest(url, body)
        return responseMono.map { response ->
            logger.debug("request to send mail submitted - response statusCode ${response.statusCode}")
            if (response.statusCode != 200) {
                logger.error(response.statusCode.toString() + " " + response.responseMessage)
                throw MailNotSentException("request to send email not successful - recipient $recipient")
            }
            true
        }
    }

    // visible for testing
    fun sendRequest(url: String, body: List<Pair<String, String>>): Mono<Response> {
        return fuel.upload(url, Method.POST, body)
                .authentication()
                .basic("api", apiKey)
                .monoResponse()
    }

}