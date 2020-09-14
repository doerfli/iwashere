package li.doerf.iwashere.utils

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.accounts.UserRepository
import li.doerf.iwashere.infrastructure.security.UserPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.security.Principal
import kotlin.random.Random

@Component
class UserHelper(
        private val userRepository: UserRepository
) {
    private val logger = getLogger(javaClass)

    internal fun getUser(principal: Principal): User {
        logger.trace("getUser by Princical")
        if ((principal as UsernamePasswordAuthenticationToken).principal is org.springframework.security.core.userdetails.User) {
            logger.warn("this should only be used during test")
            // this is for testcases using withMockUser
            val user = principal.principal as org.springframework.security.core.userdetails.User
            return userRepository.findFirstByUsername(user.username).get()
        }

        val userPrincipal = principal.principal as UserPrincipal
        return userPrincipal.user
    }

    fun createUniqueToken(): String {
        var token: String

        do {
            token = generateToken()
        } while( userRepository.countByToken(token) > 0)

        return token
    }

    companion object {
        private val charPool: List<Char> = ('A'..'Z') + ('0'..'9')

        fun generateToken(): String {
            return (1..8)
                    .map { _ -> Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("")
        }
    }
}

