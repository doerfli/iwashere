package li.doerf.iwashere.utils

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.security.UserPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.security.Principal

@Component
class UserHelper(
        private val userRepository: UserRepository
) {
    private val logger = getLogger(javaClass)

    internal fun getUser(principal: Principal): User {
        if ((principal as UsernamePasswordAuthenticationToken).principal is org.springframework.security.core.userdetails.User) {
            logger.warn("this should only be used during test")
            // this is for testcases using withMockUser
            val user = (principal as UsernamePasswordAuthenticationToken).principal as org.springframework.security.core.userdetails.User
            return userRepository.findFirstByUsername(user.username).get()
        }

        val username = (principal as UsernamePasswordAuthenticationToken).principal as UserPrincipal
        return username.user
    }
}

