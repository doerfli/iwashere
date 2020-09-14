package li.doerf.iwashere.infrastructure.mail.mq

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel

interface MailStreams {

    @Input
    fun mailIn(): SubscribableChannel

    @Output
    fun mailOut(): MessageChannel
}