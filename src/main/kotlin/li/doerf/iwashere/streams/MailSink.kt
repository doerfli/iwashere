package li.doerf.iwashere.streams

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.services.mail.MailgunService
import li.doerf.iwashere.utils.getLogger
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.handler.annotation.Payload

@EnableBinding(MailStreams::class)
class MailSink(val mailgunService: MailgunService) {

    private val logger = getLogger(javaClass)

    @StreamListener("mailIn")
    fun handleSend(@Payload request: SendMailMessage) {
        logger.debug("sending mail to ${request.recipient} (${request.id})")

        runBlocking {
            mailgunService.sendEmail(
                    request.sender,
                    request.recipient,
                    request.subject,
                    request.text
            )
        }
        logger.debug("mail sent (${request.id})")
    }

}