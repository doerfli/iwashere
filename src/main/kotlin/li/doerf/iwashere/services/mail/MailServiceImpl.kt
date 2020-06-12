package li.doerf.iwashere.services.mail

import li.doerf.iwashere.documents.User
import li.doerf.iwashere.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import reactor.core.publisher.Mono

@Service
class MailServiceImpl @Autowired constructor(
        private val templateEngine: TemplateEngine,
        private val mailgunService: MailgunServiceImpl
) : MailService {

    @Value("\${baseUrl}")
    private lateinit var applBaseUrl: String
    @Value("\${mailsender}")
    private lateinit var mailSender: String


    private val logger = getLogger(javaClass)

    override fun sendSignupMail(user: User): Mono<Boolean> {
        logger.debug("sending signup email to $user")
        val ctx = Context()
//        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        ctx.setVariable("email", user.username)
//        ctx.setVariable("link", "$applBaseUrl/#/confirmation/${user.token}")
//        ctx.setVariable("validUntil", dateFormat.format(Date.from(user.tokenExpiration)))

        val content = templateEngine.process("signup.txt", ctx)
        return mailgunService.sendEmail(
                mailSender,
                user.username,
                "Welcome to subscribed",
                content)
    }


}