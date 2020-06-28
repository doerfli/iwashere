package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@NoArgs
data class Visitor(
        @Id @GeneratedValue val id: Long? = null,
        var name: String,
        var email: String,
        var phone: String,
        val registrationDate: Instant = Instant.now()
)