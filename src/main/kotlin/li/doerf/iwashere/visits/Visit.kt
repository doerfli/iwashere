package li.doerf.iwashere.visits

import li.doerf.iwashere.guests.Guest
import li.doerf.iwashere.locations.Location
import li.doerf.iwashere.utils.NoArgs
import li.doerf.iwashere.utils.now
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@NoArgs
data class Visit(
        @Id @GeneratedValue val id: Long? = null,
        @ManyToOne
        @JoinColumn(name="guest_id", nullable=false)
        val guest: Guest,
        @ManyToOne
        @JoinColumn(name="location_id", nullable=false)
        val location: Location,
        val visitTimestamp: LocalDateTime = now(),
        var verifiedEmail: Boolean = false,
        var verifiedPhone: Boolean = false
)