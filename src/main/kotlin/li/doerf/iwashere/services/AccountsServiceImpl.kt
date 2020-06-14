package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.mail.MailService
import li.doerf.iwashere.utils.getLogger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountsServiceImpl(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val mailService: MailService
) : AccountsService {

    private val logger = getLogger(javaClass)

    override suspend fun create(username: String, password: String): User {
        val pwdHash = passwordEncoder.encode(password)
        val userOpt = userRepository.findFirstByUsername(username)

        if (userOpt.isPresent) {
            logger.warn("user already exists: ${userOpt.get()}")
            return userOpt.get()
        }

        val newUser = userRepository.save(User(username = username, password = pwdHash))
        logger.debug("user created")
        mailService.sendSignupMail(newUser)

        return newUser
    }

}