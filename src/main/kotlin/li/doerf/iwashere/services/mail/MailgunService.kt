package li.doerf.iwashere.services.mail

import reactor.core.publisher.Mono

interface MailgunService {
    fun sendEmail(sender: String, recipient: String, subject: String, text: String): Mono<Boolean>
}