package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import java.time.Instant
import javax.persistence.*

@Entity
@NoArgs
data class Location(
        @Id @GeneratedValue val id: Long? = null,
        var name: String,
        var shortname: String,
        var street: String?,
        var zip: String?,
        var city: String?,
        var country: String?,
        val registrationDate: Instant = Instant.now(),
        @ManyToOne
        @JoinColumn(name="user_id", nullable=false)
        val user: User
)