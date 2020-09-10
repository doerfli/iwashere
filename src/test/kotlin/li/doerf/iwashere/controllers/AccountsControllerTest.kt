package li.doerf.iwashere.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import li.doerf.iwashere.TestHelper
import li.doerf.iwashere.dto.account.SignupRequest
import li.doerf.iwashere.entities.AccountState
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.AccountsServiceImpl
import li.doerf.iwashere.services.mail.MailService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@ExtendWith(SpringExtension::class)
@Import(TestHelper::class, AccountsServiceImpl::class, UserHelper::class)
@WebMvcTest(value = [AccountsController::class])
internal class AccountsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockkBean
    private lateinit var userRepository: UserRepository
    @MockkBean
    private lateinit var mailService: MailService
    @MockkBean
    private lateinit var userHelper: UserHelper

    @Test
    fun signup() {
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        every { userHelper.createUniqueToken() } returns "xyzabc"
        every { userRepository.save(any<User>()) } returns User(null, "newuser", "xx", token = UserHelper.generateToken())
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        mockMvc.perform(post("/accounts/signup")
                .content(TestHelper.asJsonString(SignupRequest("newuser", "123456")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk)

        coVerify {
            userRepository.save(allAny<User>())
            mailService.sendSignupMail(any())
        }
    }

    @Test
    fun signupUserExists() {
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(User(null, "newuser", "xx", token = UserHelper.generateToken(), state = AccountState.CONFIRMED))

        mockMvc.perform(post("/accounts/signup")
                .content(TestHelper.asJsonString(SignupRequest("newuser", "123456")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk)

        coVerify(exactly = 0) {
            userRepository.save(allAny<User>())
            mailService.sendSignupMail(any())
        }
    }

    @Test
    fun confirm() {
        val token = "abcdef"
        every { userRepository.findFirstByToken(any()) } returns Optional.of(User(null, "newuser", "xx", token = token, tokenValidUntil = now().plusMinutes(5)))
        every { userRepository.save(any<User>()) } returns User(null, "newuser", "xx", token = null)

        mockMvc.perform(post("/accounts/confirm/$token")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk)

        verify {
            userRepository.save(allAny<User>())
        }
    }

    @Test
    fun confirmTokenNotFound() {
        val token = "abcdef"
        every { userRepository.findFirstByToken(any()) } returns Optional.empty()

        mockMvc.perform(post("/accounts/confirm/$token")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest)

        verify(exactly = 0) {
            userRepository.save(allAny<User>())
        }
    }

    @Test
    fun confirmTokenExpired() {
        val token = "abcdef"
        every { userRepository.findFirstByToken(any()) } returns Optional.of(User(null, "newuser", "xx", token = token, tokenValidUntil = now().minusMinutes(5)))

        mockMvc.perform(post("/accounts/confirm/$token")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isUnauthorized)

        verify(exactly = 0) {
            userRepository.save(allAny<User>())
        }
    }

    @Test
    fun confirmInvalidState() {
        val token = "abcdef"
        every { userRepository.findFirstByToken(any()) } returns Optional.of(User(null, "newuser", "xx", token = token, tokenValidUntil = now().minusMinutes(5), state = AccountState.CONFIRMED))

        mockMvc.perform(post("/accounts/confirm/$token")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isConflict)

        verify(exactly = 0) {
            userRepository.save(allAny<User>())
        }
    }
}