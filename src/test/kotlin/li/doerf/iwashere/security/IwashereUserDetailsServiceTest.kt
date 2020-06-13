package li.doerf.iwashere.security

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import li.doerf.iwashere.documents.User
import li.doerf.iwashere.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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

    @Test
    fun testLoadUserByUsername() {
        val username = "someone"
        val userMock = mockkClass(User::class)
        every { userMock.username } returns "someone"
        every { userRepository.findFirstByUsername(any()) } returns Optional.of(userMock)

        val res = sut.loadUserByUsername(username)

        assertThat(res).isInstanceOf(UserPrincipal::class.java)
        assertThat(res.username).isEqualTo(username)
    }
}