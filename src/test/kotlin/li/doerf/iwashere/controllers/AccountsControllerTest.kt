package li.doerf.iwashere.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import li.doerf.iwashere.TestHelper
import li.doerf.iwashere.dto.SignupRequest
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.services.AccountsServiceImpl
import li.doerf.iwashere.services.mail.MailService
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
@Import(TestHelper::class, AccountsServiceImpl::class)
@WebMvcTest(value = [AccountsController::class])
internal class AccountsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var testHelper: TestHelper
    @MockkBean
    private lateinit var userRepository: UserRepository
    @MockkBean
    private lateinit var mailService: MailService

    @Test
    fun signup() {
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        every { userRepository.save(any<User>()) } returns User(null, "newuser", "xx")
        coEvery { mailService.sendSignupMail(any()) } returns mockk()

        mockMvc.perform(post("/accounts/signup")
                .content(testHelper.asJsonString(SignupRequest("newuser", "123456")))
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
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(User(null, "newuser", "xx"))

        mockMvc.perform(post("/accounts/signup")
                .content(testHelper.asJsonString(SignupRequest("newuser", "123456")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk)

        coVerify(exactly = 0) {
            userRepository.save(allAny<User>())
            mailService.sendSignupMail(any())
        }
    }

}