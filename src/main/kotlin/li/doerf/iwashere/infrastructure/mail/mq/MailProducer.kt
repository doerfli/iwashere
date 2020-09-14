package li.doerf.iwashere.infrastructure.mail.mq

import org.springframework.cloud.stream.annotation.EnableBinding

@EnableBinding(MailStreams::class)
class MailProducer(val mailStreams: MailStreams) {
}