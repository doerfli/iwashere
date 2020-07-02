package li.doerf.iwashere.entities

import li.doerf.iwashere.utils.NoArgs
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "appluser")
@NoArgs
data class User(
        @Id @GeneratedValue
        val id: Long? = null,
        val username: String,
        var password: String,
        var passwordChangedDate: LocalDateTime = LocalDateTime.now(),
        @Enumerated(EnumType.STRING)
        var state: AccountState = AccountState.UNCONFIRMED,
        @Enumerated(EnumType.STRING)
        var language: Language = Language.DE,
        var token: String?,
        val createdDate: LocalDateTime = LocalDateTime.now()
)