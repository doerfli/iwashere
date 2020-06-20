package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@NoArgs
data class Visitor(
        @Id @GeneratedValue val id: Long? = null,
        var firstname: String,
        var lastname: String,
        var email: String,
        var phone: String
)