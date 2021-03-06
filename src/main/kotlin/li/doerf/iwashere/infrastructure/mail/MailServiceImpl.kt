package li.doerf.iwashere.infrastructure.mail

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.infrastructure.mail.mq.MailProducer
import li.doerf.iwashere.infrastructure.mail.mq.SendMailMessage
import li.doerf.iwashere.utils.getLogger
import li.doerf.iwashere.visits.Visit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.ZoneId
import java.time.ZoneOffset
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
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        ctx.setVariable("email", user.username)
        ctx.setVariable("link", "$applBaseUrl/#/signupConfirm/${user.token}")
        // assume LocalDateTime to be in UTC and convert to Zurich time
        val toInstant = user.tokenValidUntil?.toInstant(ZoneOffset.of("Z"))?.atZone(ZoneId.of("Europe/Zurich"))
        ctx.setVariable("validUntil", dateFormat.format(toInstant))

        val content = templateEngine.process("signup_${user.language.lower()}.txt", ctx)

        sendMail("Willkommen bei 'Ich war da'", user.username, content)
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
        val visitTimestamp = visit.visitTimestamp.toInstant(ZoneOffset.of("Z"))?.atZone(ZoneId.of("Europe/Zurich"))
        ctx.setVariable("visit_timestamp", dateFormat.format(visitTimestamp))
        ctx.setVariable("link", "$applBaseUrl/#/visit/${visit.id}/confirm")

        val content = templateEngine.process("visit_${visit.location.user.language.lower()}.txt", ctx)
        sendMail("Ihr Besuch bei '${visit.location.name}'", recipientEmail, content)
    }

    override suspend fun sendForgotPasswordMail(user: User) {
        logger.debug("sending forgot password email to $user")
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val ctx = Context()
        ctx.setVariable("email", user.username)
        ctx.setVariable("link", "$applBaseUrl/#/resetPassword/${user.token}")
        val tokenValidUntil = user.tokenValidUntil?.toInstant(ZoneOffset.of("Z"))?.atZone(ZoneId.of("Europe/Zurich"))
        ctx.setVariable("validUntil", dateFormat.format(tokenValidUntil))

        val content = templateEngine.process("forgotPassword_${user.language.lower()}.txt", ctx)

        sendMail("Passwort vergessen (Ich war da)", user.username, content)
    }

    override suspend fun sendPasswordResetMail(user: User) {
        logger.debug("sending password reset email to $user")
        val ctx = Context()
        ctx.setVariable("email", user.username)

        val content = templateEngine.process("passwordReset_${user.language.lower()}.txt", ctx)

        sendMail("Passwort zurückgesetzt (Ich war da)", user.username, content)
    }

    override suspend fun sendFeedback(name: String, email: String, message: String) {
        logger.debug("sending feedback email")
        val ctx = Context()
        ctx.setVariable("email", email)
        ctx.setVariable("name", name)
        ctx.setVariable("message", message)

        val content = templateEngine.process("feedback.txt", ctx)
        sendMail("Feedback 'Ich war da'", "info@ich-war-da.net", content)
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