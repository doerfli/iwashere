package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.entities.AccountState
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.mail.MailService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
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

    @Test
    fun create() {
        every { passwordEncoder.encode("test") } returns fakePwd
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        val userMock = mockkClass(User::class)
        every { userRepository.save(any<User>()) } returns userMock
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

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
    fun createUserExists() {
        every { passwordEncoder.encode(any()) } returns fakePwd
        val userMock = mockkClass(User::class)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

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
    fun confirm() {
        // GIVEN
        val token = "abcdef"
        val user = User(null, "user", "xx", token = token)
        val userSlot = slot<User>()
        every { userRepository.findFirstByToken(any()) } returns Optional.of(user)
        every { userRepository.save(capture(userSlot)) } returns user

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

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

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

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

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

        // WHEN
        assertThatThrownBy {
            svc.confirm("abcdef")
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun changePassword() {
        // GIVEN
        every { passwordEncoder.matches("test", "xxx") } returns true
        every { passwordEncoder.encode("other") } returns "yyy"
        val lastChangeDate = LocalDateTime.now().minusDays(1)
        val userMock = User(null, "user", "xxx", token = "abcdef", state = AccountState.CONFIRMED, passwordChangedDate = lastChangeDate)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } returns userMock

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

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

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

        // WHEN
        assertThatThrownBy {
            svc.changePassword("old", "new", "test")
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("username")
    }

    @Test
    fun changePasswordInvalidOldPassword() {
        // GIVEN
        every { passwordEncoder.matches("test", "xx") } returns false
        val lastChangeDate = LocalDateTime.now().minusDays(1)
        val userMock = User(null, "user", "xx", token = "abcdef", state = AccountState.CONFIRMED, passwordChangedDate = lastChangeDate)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        val svc = AccountsServiceImpl(userRepository, passwordEncoder, mailService)

        assertThatThrownBy {
            svc.changePassword("test", "other", "user")
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("password")

        verify(exactly = 0) {
            passwordEncoder.encode("other")
            userRepository.save(any() as User)
        }
    }
}