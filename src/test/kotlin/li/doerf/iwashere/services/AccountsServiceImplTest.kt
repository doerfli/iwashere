package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.documents.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.mail.MailService
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

}