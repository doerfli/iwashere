package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
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
        val registrationDate: LocalDateTime = LocalDateTime.now()
)