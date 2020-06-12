package li.doerf.iwashere.services.mail

import li.doerf.iwashere.documents.User
import reactor.core.publisher.Mono

interface MailService {
    fun sendSignupMail(user: User): Mono<Boolean>
}