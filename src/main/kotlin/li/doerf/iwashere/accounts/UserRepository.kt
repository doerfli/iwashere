package li.doerf.iwashere.accounts

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface UserRepository : JpaRepository<User, Long> {
    fun findFirstByUsername(username: String): Optional<User>
    fun findFirstByToken(token: String): Optional<User>
    fun countByToken(token: String): Long
}