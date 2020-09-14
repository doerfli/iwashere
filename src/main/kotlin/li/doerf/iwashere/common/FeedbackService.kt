package li.doerf.iwashere.common

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.infrastructure.mail.MailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FeedbackService @Autowired constructor(
        private val mailService: MailService
){

    fun send(name: String, email: String, message: String) {
        runBlocking {
            mailService.sendFeedback(name, email, message)
        }
    }

}
