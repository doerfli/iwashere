package li.doerf.iwashere.services.mail

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.streams.MailProducer
import li.doerf.iwashere.streams.SendMailMessage
import li.doerf.iwashere.utils.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.format.DateTimeFormatter

@Service
class MailServiceImpl @Autowired constructor(
        private val templateEngine: TemplateEngine,
        private val mailProducer: MailProducer
) : MailService {

    @Value("\${baseUrl}")
    private lateinit var applBaseUrl: String
    @Value("\${mailsender}")
    private lateinit var mailSender: String


    private val logger = getLogger(javaClass)

    override suspend fun sendSignupMail(user: User) {
        logger.debug("sending signup email to $user")
        val ctx = Context()
//        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        ctx.setVariable("email", user.username)
        // TODO url via config
        ctx.setVariable("link", "$applBaseUrl/#/signupConfirm/${user.token}")
//        ctx.setVariable("validUntil", dateFormat.format(Date.from(user.tokenExpiration)))

        val content = templateEngine.process("signup_${user.language.lower()}.txt", ctx)

        sendMail("Welcome to iwashere", user.username, content)
    }

    override suspend fun sendVisitMail(visit: Visit) {
        if (visit.guest.email.isEmpty()) {
            logger.warn("no email set - not sending email")
            return
        }

        val recipientEmail = visit.guest.email
        logger.debug("sending visit email to $recipientEmail"
        )
        val ctx = Context()
        ctx.setVariable("guest_name", visit.guest.name)
        ctx.setVariable("guest_email", visit.guest.email)
        ctx.setVariable("location_name", visit.location.name)
        ctx.setVariable("location_zip", visit.location.zip)
        ctx.setVariable("location_city", visit.location.city)
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        ctx.setVariable("visit_timestamp", dateFormat.format(visit.visitTimestamp))

        val content = templateEngine.process("visit_${visit.location.user.language.lower()}.txt", ctx)
        sendMail("Ihr Besuch bei ${visit.location.name}", recipientEmail, content)
    }

    override suspend fun sendForgotPasswordMail(user: User) {
        logger.debug("sending forgot password email to $user")
        val ctx = Context()
        ctx.setVariable("email", user.username)
        // TODO url via config
        ctx.setVariable("link", "$applBaseUrl/#/resetPassword/${user.token}")
//        ctx.setVariable("validUntil", dateFormat.format(Date.from(user.tokenExpiration)))

        val content = templateEngine.process("forgotPassword_${user.language.lower()}.txt", ctx)

        sendMail("iwashere passwort forgotten", user.username, content)
    }

    override suspend fun sendPasswordResetMail(user: User) {
        logger.debug("sending password reset email to $user")
        val ctx = Context()
        ctx.setVariable("email", user.username)

        val content = templateEngine.process("passwordReset_${user.language.lower()}.txt", ctx)

        sendMail("iwashere passwort reset", user.username, content)
    }

    private fun sendMail(subject: String, recipient: String, content: String) {
        val msg = MessageBuilder.withPayload(
                SendMailMessage(mailSender,
                        recipient,
                        subject,
                        content)
        ).build()
        mailProducer.mailStreams.mailOut().send(msg)
        logger.debug("mail message ${msg.payload.id} queued")
    }

}