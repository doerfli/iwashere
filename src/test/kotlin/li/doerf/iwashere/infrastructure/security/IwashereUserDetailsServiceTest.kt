package li.doerf.iwashere.infrastructure.security

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import li.doerf.iwashere.accounts.AccountState
import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.accounts.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@Import(UserRepository::class)
internal class IwashereUserDetailsServiceTest {
    private lateinit var sut: IwashereUserDetailsService
    @MockkBean
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        sut = IwashereUserDetailsService(userRepository)
    }

    @Test
    fun testLoadUserByUsernameNotFound() {
        every { userRepository.findFirstByUsername(any()) } returns Optional.empty()
        org.junit.jupiter.api.assertThrows<NoSuchElementException> {
            sut.loadUserByUsername("doesnotexist")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [ "CONFIRMED", "RESET_PASSWORD" ])
    fun testLoadUserByUsername(state: String) {
        val username = "someone"
        val userMock = mockkClass(User::class)
        every { userMock.username } returns "someone"
        every { userMock.state } returns AccountState.valueOf(state)
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        val res = sut.loadUserByUsername(username)

        assertThat(res).isInstanceOf(UserPrincipal::class.java)
        assertThat(res.username).isEqualTo(username)
    }

    @Test
    fun testLoadUserByUsernameUnconfirmed() {
        val username = "someone@mail.com"
        val userMock = mockkClass(User::class)
        every { userMock.username } returns username
        every { userMock.state } returns AccountState.UNCONFIRMED
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            sut.loadUserByUsername(username)
        }
    }
}