package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "appluser")
@NoArgs
data class User(
        @Id @GeneratedValue
        val id: Long? = null,
        val username: String,
        var password: String,
        var language: Language = Language.DE,
        val registrationDate: Instant = Instant.now()
//        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
//        var locations: List<Location>? = null
)