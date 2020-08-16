package li.doerf.iwashere.streams

import org.springframework.cloud.stream.annotation.EnableBinding

@EnableBinding(MailStreams::class)
class MailProducer(val mailStreams: MailStreams) {
}