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
        every { passwordEncoder.encode(any()) } returns fakePwd
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        var userMock = mockkClass(User::class)
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
            svc.confirm("abcdef");
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
            svc.confirm("abcdef");
        }.isInstanceOf(IllegalStateException::class.java)
    }
}