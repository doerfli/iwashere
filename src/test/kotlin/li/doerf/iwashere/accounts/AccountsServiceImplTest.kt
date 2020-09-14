package li.doerf.iwashere.accounts

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.infrastructure.mail.MailService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.now
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
internal class AccountsServiceImplTest {

    private val fakePwd = "absedefg"
    @MockkBean
    private lateinit var passwordEncoder: PasswordEncoder
    @MockkBean
    private lateinit var userRepository: UserRepository
    @MockkBean
    private lateinit var mailService: MailService
    @MockkBean
    private lateinit var userHelper: UserHelper
    private lateinit var svc: AccountsServiceImpl

    @BeforeEach
    fun setup() {
        svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService, userHelper)
    }

    @Test
    fun create() {
        every { passwordEncoder.encode("test") } returns fakePwd
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        every { userHelper.createUniqueToken() } returns "xyzabc"
        val userMock = mockkClass(User::class)
        every { userRepository.save(any<User>()) } returns userMock
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        runBlocking {
            svc.create("test@bla.com", "test")
        }

        coVerify {
            userRepository.save(allAny<User>())
            passwordEncoder.encode("test")
            mailService.sendSignupMail(userMock)
        }
    }

    @Test
    fun createUserExistsConfirmed() {
        every { passwordEncoder.encode(any()) } returns fakePwd
        val userMock = mockkClass(User::class)
        every { userMock.state } returns AccountState.CONFIRMED
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        runBlocking {
            svc.create("test@bla.com", "test")
        }

        verify {
            passwordEncoder.encode("test")
        }
        coVerify(exactly = 0) {
            userRepository.save(allAny<User>())
            mailService.sendSignupMail(userMock)
        }
    }

    @Test
    fun createUserExistsUnconfirmed() {
        every { passwordEncoder.encode(any()) } returns fakePwd
        val userMock = mockkClass(User::class)
        every { userMock.state } returns AccountState.UNCONFIRMED
        every { userMock.tokenValidUntil } returns now().plusMinutes(5)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        runBlocking {
            svc.create("test@bla.com", "test")
        }

        coVerify(exactly = 0) {
            userRepository.save(allAny<User>())
        }
        coVerify {
            passwordEncoder.encode("test")
            mailService.sendSignupMail(userMock)
        }
    }

    @Test
    fun createUserExistsUnconfirmedAndExpired() {
        every { passwordEncoder.encode(any()) } returns fakePwd
        every { userHelper.createUniqueToken() } returns "1234"
        val userMock = mockkClass(User::class)
        every { userMock.state } returns AccountState.UNCONFIRMED
        every { userMock.tokenValidUntil } returns now().minusMinutes(5)
        val userMock2 = mockkClass(User::class)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)
        every { userRepository.delete(userMock) } returns mockk()
        every { userRepository.save(any<User>()) } returns userMock2
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        runBlocking {
            svc.create("test@bla.com", "test")
        }

        coVerify {
            passwordEncoder.encode("test")
            userRepository.delete(userMock)
            userRepository.save(allAny<User>())
            mailService.sendSignupMail(userMock2)
        }
    }

    @Test
    fun confirm() {
        // GIVEN
        val token = "abcdef"
        val user = User(null, "user", "xx", token = token, tokenValidUntil = now().plusMinutes(5))
        val userSlot = slot<User>()
        every { userRepository.findFirstByToken(any()) } returns Optional.of(user)
        every { userRepository.save(capture(userSlot)) } returns user

        // WHEN
        svc.confirm(token)

        // THEN
        assertThat(userSlot.captured.token).isNull()
        assertThat(userSlot.captured.state).isEqualTo(AccountState.CONFIRMED)
    }

    @Test
    fun confirmNotExists() {
        // GIVEN
        every { userRepository.findFirstByToken(any()) } returns Optional.empty()

        // WHEN
        assertThatThrownBy {
            svc.confirm("abcdef")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun confirmInvalidState() {
        // GIVEN
        val token = "abcdef"
        val user = User(null, "user", "xx", token = token, state = AccountState.CONFIRMED)
        every { userRepository.findFirstByToken(any()) } returns Optional.of(user)

        // WHEN
        assertThatThrownBy {
            svc.confirm("abcdef")
        }.isInstanceOf(InvalidUserStateException::class.java)
    }

    @Test
    fun confirmTokenExpired() {
        // GIVEN
        val token = "abcdef"
        val user = User(null, "user", "xx", token = token, tokenValidUntil = now().minusMinutes(1))
        every { userRepository.findFirstByToken(any()) } returns Optional.of(user)

        // WHEN
        assertThatThrownBy {
            svc.confirm("abcdef")
        }.isInstanceOf(ExpiredTokenException::class.java)
    }

    @Test
    fun changePassword() {
        // GIVEN
        every { passwordEncoder.matches("test", "xxx") } returns true
        every { passwordEncoder.encode("other") } returns "yyy"
        val lastChangeDate = now().minusDays(1)
        val userMock = User(null, "user", "xxx", token = "abcdef", state = AccountState.CONFIRMED, passwordChangedDate = lastChangeDate)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } returns userMock

        svc.changePassword("test", "other", "user")

        verify {
            passwordEncoder.encode("other")
        }
        val savedUser = userSlot.captured
        assertThat(savedUser.password).isEqualTo("yyy")
        assertThat(savedUser.passwordChangedDate).isAfter(lastChangeDate)
    }

    @Test
    fun changePasswordUserNotExists() {
        // GIVEN
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()

        // WHEN
        assertThatThrownBy {
            svc.changePassword("old", "new", "test")
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("username")
    }

    @Test
    fun changePasswordInvalidOldPassword() {
        // GIVEN
        every { passwordEncoder.matches("test", "xx") } returns false
        val lastChangeDate = now().minusDays(1)
        val userMock = User(null, "user", "xx", token = "abcdef", state = AccountState.CONFIRMED, passwordChangedDate = lastChangeDate)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        assertThatThrownBy {
            svc.changePassword("test", "other", "user")
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("password")

        verify(exactly = 0) {
            passwordEncoder.encode("other")
            userRepository.save(any() as User)
        }
    }

    @Test
    fun forgotPasswort() {
        val user = User(null, "user", "xx", state = AccountState.CONFIRMED)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(user)
        every { userHelper.createUniqueToken() } returns "xyzabc"
        every { userRepository.save(any() as User) } returns user
        coEvery { mailService.sendForgotPasswordMail(any()) } returns mockk()

        runBlocking {
            svc.forgotPassword("user")
        }

        assertThat(user.token).isEqualTo("xyzabc")
        assertThat(user.state).isEqualTo(AccountState.RESET_PASSWORD)

        coVerify {
            mailService.sendForgotPasswordMail(user)
        }
    }

    @Test
    fun forgotPasswortInvalidUser() {
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()

        runBlocking {
            svc.forgotPassword("user")
        }

        coVerify(exactly = 0) {
            mailService.sendForgotPasswordMail(any() as User)
            userRepository.save(any() as User)
        }
    }

    @Test
    fun resetPasswort() {
        val date = now().minusDays(1)
        val user = User(null, "user", "xx", token = "token123", tokenValidUntil = now().plusMinutes(5), state = AccountState.RESET_PASSWORD, passwordChangedDate = date)
        every { userRepository.findFirstByToken("token123") } returns Optional.of(user)
        every { passwordEncoder.encode("newpwd") } returns "yyy"
        every { userRepository.save(any() as User) } returns user
        coEvery { mailService.sendPasswordResetMail(user) } returns mockk()

        runBlocking {
            svc.resetPassword("token123", "newpwd")
        }

        assertThat(user.token).isNull()
        assertThat(user.state).isEqualTo(AccountState.CONFIRMED)
        assertThat(user.password).isEqualTo("yyy")
        assertThat(user.passwordChangedDate).isAfter(date)

        coVerify {
            mailService.sendPasswordResetMail(user)
            userRepository.save(user)
        }
    }

    @Test
    fun resetPasswortTokenInvalid() {
        every { userRepository.findFirstByToken("token123") } returns Optional.empty()

        assertThatThrownBy {
            runBlocking {
                svc.resetPassword("token123", "newpwd")
            }
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("invalid token")

        coVerify(exactly = 0) {
            mailService.sendPasswordResetMail(any() as User)
            userRepository.save(any() as User)
        }
    }

    @Test
    fun resetPasswortInvalidState() {
        val user = User(null, "user", "xx", token = "token123", state = AccountState.CONFIRMED)
        every { userRepository.findFirstByToken("token123") } returns Optional.of(user)

        assertThatThrownBy {
            runBlocking {
                svc.resetPassword("token123", "newpwd")
            }
        }.isInstanceOf(InvalidUserStateException::class.java)

        coVerify(exactly = 0) {
            mailService.sendPasswordResetMail(any() as User)
            userRepository.save(any() as User)
        }
    }

    @Test
    fun resetPasswortTokenExpired() {
        val user = User(null, "user", "xx", token = "token123", state = AccountState.RESET_PASSWORD, tokenValidUntil = now().minusMinutes(1))
        every { userRepository.findFirstByToken("token123") } returns Optional.of(user)

        assertThatThrownBy {
            runBlocking {
                svc.resetPassword("token123", "newpwd")
            }
        }.isInstanceOf(ExpiredTokenException::class.java)

        coVerify(exactly = 0) {
            mailService.sendPasswordResetMail(any() as User)
            userRepository.save(any() as User)
        }
    }
}