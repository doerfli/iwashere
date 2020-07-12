package li.doerf.iwashere.services

import li.doerf.iwashere.entities.AccountState
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.mail.MailService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.getLogger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AccountsServiceImpl(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val mailService: MailService,
        private val userHelper: UserHelper
) : AccountsService {

    private val logger = getLogger(javaClass)

    override suspend fun create(username: String, password: String): User {
        val pwdHash = passwordEncoder.encode(password)
        val userOpt = userRepository.findFirstByUsername(username)

        if (userOpt.isPresent) {
            logger.warn("user already exists: ${userOpt.get()}")
            return userOpt.get()
        }

        val newUser = userRepository.save(User(
                username = username,
                password = pwdHash,
                token = userHelper.createUniqueToken()
        ))
        logger.debug("user created")
        mailService.sendSignupMail(newUser)

        return newUser
    }

    override fun confirm(token: String): User {
        val userOpt = userRepository.findFirstByToken(token)

        if (userOpt.isEmpty) {
            throw IllegalArgumentException("invalid token: $token")
        }

        val user = userOpt.get()

        if (user.state != AccountState.UNCONFIRMED) {
            throw IllegalStateException("account has invalid state")
        }

        logger.info("found unconfirmed user $user")
        user.state = AccountState.CONFIRMED
        user.token = null

        try {
            return userRepository.save(user)
        } finally {
            logger.info("User confirmed: $user")
        }
    }

    override fun changePassword(oldPassword: String, newPassword: String, username: String) {
        logger.trace("changing password for user $username")
        val userOpt = userRepository.findFirstByUsername(username)

        if (userOpt.isEmpty) {
            throw IllegalArgumentException("invalid username")
        }
        val user = userOpt.get()

        checkPassword(oldPassword, user)

        setPassword(newPassword, user)
        userRepository.save(user)
        logger.info("Password changed for user $user")
    }

    override suspend fun forgotPassword(username: String) {
        logger.trace("forgot password for user $username")
        val userOpt = userRepository.findFirstByUsername(username)

        if (userOpt.isEmpty) {
            logger.warn("invalid username")
            // continue as if user exists for outside world
            return
        }
        val user = userOpt.get()

        user.state = AccountState.RESET_PASSWORD
        user.token = userHelper.createUniqueToken()
        userRepository.save(user)

        mailService.sendForgotPasswordMail(user)

        logger.info("forgot password for user $username done")
    }

    override suspend fun resetPassword(token: String, password: String) {
        logger.trace("forgot password for user with token $token")
        val userOpt = userRepository.findFirstByToken(token)

        if (userOpt.isEmpty) {
            throw IllegalArgumentException("invalid token")
        }
        val user = userOpt.get()

        if (user.state != AccountState.RESET_PASSWORD) {
            throw java.lang.IllegalStateException("user did not request passwort reset")
        }

        setPassword(password, user)
        user.state = AccountState.CONFIRMED
        user.token = null
        userRepository.save(user)

        mailService.sendPasswordResetMail(user)

        logger.info("reset password for user $user done")
    }

    private fun checkPassword(password: String, user: User) {
        if (! passwordEncoder.matches(password, user.password)) {
            logger.warn("password did not match")
            throw IllegalArgumentException("invalid password")
        }
    }

    private fun setPassword(password: String, user: User) {
        val newPwdHash = passwordEncoder.encode(password)
        user.password = newPwdHash
        user.passwordChangedDate = LocalDateTime.now()
    }

}