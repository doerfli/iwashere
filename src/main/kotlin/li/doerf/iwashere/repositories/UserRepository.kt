package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.User
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*


interface UserRepository : PagingAndSortingRepository<User, Long> {
    fun findFirstByUsername(username: String): Optional<User>
    fun findFirstByToken(token: String): Optional<User>
}