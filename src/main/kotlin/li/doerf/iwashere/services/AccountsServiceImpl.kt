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
import java.util.*

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
        var userOpt = userRepository.findFirstByUsername(username)

        if (userOpt.isPresent && userOpt.get().state == AccountState.UNCONFIRMED && LocalDateTime.now().isAfter(userOpt.get().tokenValidUntil)) {
            deleteUser(userOpt)
            userOpt = Optional.empty()
        } else if (userOpt.isPresent && userOpt.get().state != AccountState.UNCONFIRMED) {
            logger.warn("user already exists: ${userOpt.get()}")
            return userOpt.get()
        }

        val user = userOpt.orElseGet {
            logger.info("storing new user")
            userRepository.save(User(
                username = username,
                password = pwdHash,
                token = userHelper.createUniqueToken(),
                tokenValidUntil = LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES)
        )) }

        mailService.sendSignupMail(user)
        return user
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun deleteUser(userOpt: Optional<User>) {
        logger.debug("user already exists but token expired. Removing user")
        userRepository.delete(userOpt.get())
        logger.warn("expired user deleted")
    }

    override fun confirm(token: String): User {
        val userOpt = userRepository.findFirstByToken(token)

        if (userOpt.isEmpty) {
            logger.debug("token not found")
            throw IllegalArgumentException("invalid token: $token")
        }

        val user = userOpt.get()

        checkUserState(user, AccountState.UNCONFIRMED)
        checkTokenExpiration(user)

        logger.info("found unconfirmed user $user")
        user.state = AccountState.CONFIRMED
        user.token = null
        user.tokenValidUntil = null

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
        user.tokenValidUntil = LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES)
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

        checkUserState(user, AccountState.RESET_PASSWORD)
        checkTokenExpiration(user)

        setPassword(password, user)
        user.state = AccountState.CONFIRMED
        user.token = null
        user.tokenValidUntil = null
        userRepository.save(user)

        mailService.sendPasswordResetMail(user)

        logger.info("reset password for user $user done")
    }

    private fun checkUserState(user: User, expectedState: AccountState) {
        if (user.state != expectedState) {
            throw InvalidUserStateException(expectedState, user.state)
        }
    }

    private fun checkTokenExpiration(user: User) {
        if (user.tokenValidUntil == null) {
            throw IllegalStateException("token expiration missing")
        }
        if (LocalDateTime.now().isAfter(user.tokenValidUntil)) {
            throw ExpiredTokenException()
        }
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

    companion object {
        private const val TOKEN_VALID_MINUTES = 1L
    }

}