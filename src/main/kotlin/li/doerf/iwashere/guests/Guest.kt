package li.doerf.iwashere.guests

import li.doerf.iwashere.utils.NoArgs
import li.doerf.iwashere.utils.now
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@NoArgs
data class Guest(
        @Id @GeneratedValue val id: Long? = null,
        var name: String,
        var email: String,
        var phone: String,
        val createdDate: LocalDateTime = now()
)