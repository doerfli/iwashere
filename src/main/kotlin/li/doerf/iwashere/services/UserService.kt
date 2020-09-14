package li.doerf.iwashere.services

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.accounts.UserRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository
) {

    private val logger = getLogger(this::class.java)

    @Cacheable("user")
    fun findByUsername(username: String): Optional<User> {
        logger.debug("lookup user by username")
        return userRepository.findFirstByUsername(username)
    }

    fun countByToken(token: String): Long {
        logger.trace("counting tokens")
        return userRepository.countByToken(token)
    }

}