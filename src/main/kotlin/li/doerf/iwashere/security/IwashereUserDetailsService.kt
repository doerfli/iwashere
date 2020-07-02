package li.doerf.iwashere.security

import li.doerf.iwashere.entities.AccountState
import li.doerf.iwashere.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class IwashereUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findFirstByUsername(username)
        if (user.isEmpty) {
            throw NoSuchElementException("user: $username");
        }
        if (user.get().state != AccountState.CONFIRMED) {
            throw IllegalStateException("account not confirmed")
        }
        return UserPrincipal(user.get())
    }

}