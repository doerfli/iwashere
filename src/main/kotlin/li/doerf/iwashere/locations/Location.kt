package li.doerf.iwashere.locations

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.utils.NoArgs
import li.doerf.iwashere.utils.now
import java.time.LocalDateTime
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
        val createdDate: LocalDateTime = now(),
        @ManyToOne
        @JoinColumn(name="user_id", nullable=false)
        val user: User,
        val useTableNumber: Boolean = false,
        val useSector: Boolean = false
)