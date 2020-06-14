package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.User
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*


interface UserRepository : PagingAndSortingRepository<User, String> {
    fun findFirstByUsername(username: String): Optional<User>
}