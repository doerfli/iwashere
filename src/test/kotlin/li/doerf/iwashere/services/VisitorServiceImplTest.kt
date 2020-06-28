package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import li.doerf.iwashere.entities.Visitor
import li.doerf.iwashere.repositories.VisitorRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@Import(VisitorRepository::class)
internal class VisitorServiceImplTest {

    private lateinit var svc: VisitorServiceImpl

    @MockkBean
    private lateinit var visitorRepository: VisitorRepository

    @BeforeEach
    fun setup() {
        svc = VisitorServiceImpl(
            visitorRepository
        )
    }

    @Test
    fun createVisitor() {
        val visitor = Visitor(1,
                "john doe",
                "john.doe@gmail.com",
                "+12235123")
        every { visitorRepository.save(any() as Visitor) } returns visitor

        val result = svc.createVisitor(
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