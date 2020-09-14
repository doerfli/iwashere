package li.doerf.iwashere.accounts

import com.ninjasquad.springmockk.MockkBean
import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.TestHelper
import li.doerf.iwashere.accounts.dto.ForgotPasswordRequest
import li.doerf.iwashere.accounts.dto.ResetPasswordRequest
import li.doerf.iwashere.infrastructure.mail.MailService
import li.doerf.iwashere.utils.getLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountsControllerIntTest {

    private val logger = getLogger(javaClass)
    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var dbTestHelper: DbTestHelper
    @Autowired
    private lateinit var userRepository: UserRepository
    @MockkBean(relaxed = true)
    private lateinit var mailService: MailService

    // BeforeEach does not work here as WithUserDetails breaks this (https://github.com/spring-projects/spring-security/issues/6591)
    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
    }

    @Test
    fun forgotAndResetPassword() {
        val username = "joe@mymail.com"
        val initialPassword = "somehash"
        val user = User( null, username, initialPassword, state = AccountState.CONFIRMED)
        userRepository.save(user)

        mvc.perform(MockMvcRequestBuilders.post("/accounts/forgotPassword")
                .content(TestHelper.asJsonString(
                        ForgotPasswordRequest(
                                username
                        )
                ))
                .contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)

        val userAfterForgotRequest = userRepository.findFirstByUsername(username).get()
        assertThat(userAfterForgotRequest.state).isEqualTo(AccountState.RESET_PASSWORD)
        val token = userAfterForgotRequest.token
        assertThat(token).isNotNull()

        mvc.perform(MockMvcRequestBuilders.post("/accounts/resetPassword")
                .content(TestHelper.asJsonString(
                        ResetPasswordRequest(
                                token!!,
                                "test1234"
                        )
                ))
                .contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)

        val userAfterResetRequest = userRepository.findFirstByUsername(username).get()
        assertThat(userAfterResetRequest.state).isEqualTo(AccountState.CONFIRMED)
        assertThat(userAfterResetRequest.token).isNull()
        assertThat(userAfterResetRequest.password).isNotEqualTo(initialPassword)
    }

}