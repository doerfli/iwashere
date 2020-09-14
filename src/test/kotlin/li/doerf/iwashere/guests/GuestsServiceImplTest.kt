package li.doerf.iwashere.guests

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@Import(GuestRepository::class)
internal class GuestsServiceImplTest {

    private lateinit var svc: GuestsServiceImpl

    @MockkBean
    private lateinit var guestRepository: GuestRepository

    @BeforeEach
    fun setup() {
        svc = GuestsServiceImpl(
            guestRepository
        )
    }

    @Test
    fun createVisitor() {
        val visitor = Guest(1,
                "john doe",
                "john.doe@gmail.com",
                "+12235123")
        every { guestRepository.save(any() as Guest) } returns visitor

        val result = svc.create(
                visitor.name,
                visitor.email,
                visitor.phone
        )

        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("john doe")
        assertThat(result.email).isEqualTo("john.doe@gmail.com")
        assertThat(result.phone).isEqualTo("+12235123")

    }
}