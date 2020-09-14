package li.doerf.iwashere.infrastructure.mail.mq

import java.util.*

data class SendMailMessage(
        val id: String,
        val sender: String,
        val recipient: String,
        val subject: String,
        val text: String
) {
    constructor(sender: String, recipient: String, subject: String, text: String):
            this(
                UUID.randomUUID().toString(),
                sender, recipient, subject, text
            )
}