package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface UserRepository : JpaRepository<User, Long> {
    fun findFirstByUsername(username: String): Optional<User>
    fun findFirstByToken(token: String): Optional<User>
    fun countByToken(token: String): Long
}