package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "appluser")
@NoArgs
data class User(
        @Id @GeneratedValue val id: Long? = null,
        val username: String,
        val password: String
)