package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import javax.persistence.*


@Entity
@Table(name = "appluser")
@NoArgs
data class User(
        @Id @GeneratedValue
        val id: Long? = null,
        val username: String,
        var password: String,
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
        var locations: List<Location>? = null
)