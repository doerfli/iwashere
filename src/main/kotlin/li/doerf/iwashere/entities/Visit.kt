package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import java.time.Instant
import javax.persistence.*

@Entity
@NoArgs
data class Visit(
        @Id @GeneratedValue val id: Long? = null,
        @ManyToOne
        @JoinColumn(name="visitor_id", nullable=false)
        val visitor: Visitor,
        @ManyToOne
        @JoinColumn(name="location_id", nullable=false)
        val location: Location,
        val registrationDate: Instant = Instant.now()
)