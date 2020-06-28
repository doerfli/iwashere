package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.entities.Visitor
import li.doerf.iwashere.repositories.VisitRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*

@ExtendWith(SpringExtension::class)
@Import(VisitRepository::class, VisitorService::class, LocationsService::class)
internal class VisitServiceImplTest {

    private lateinit var svc: VisitServiceImpl

    @MockkBean
    private lateinit var visitRepository: VisitRepository
    @MockkBean
    private lateinit var visitorService: VisitorService
    @MockkBean
    private lateinit var locationsService: LocationsService

    @BeforeEach
    fun setup() {
        svc = VisitServiceImpl(
                visitRepository,
                visitorService,
                locationsService
        )
    }

    @Test
    fun register() {
        // GIVEN
        val location = mockkClass(Location::class)
        every { locationsService.getByShortName("barometer") } returns Optional.of(location)
        val visitor = mockkClass(Visitor::class)
        every { visitorService.createVisitor("john doe",
                "john.doe@hotmail.com", "+0123456789") } returns visitor
        val visit = Visit(
                3,
                visitor,
                location,
                Instant.now()
        )
        every { visitRepository.save(any() as Visit) } returns visit

        // WHEN
        val result = svc.register("john doe",
                "john.doe@hotmail.com", "+0123456789",
                "barometer")

        // THEN
        assertThat(result).isEqualTo(visit)
    }

    @Test
    fun registerNotFound() {
        // GIVEN
        every { locationsService.getByShortName("barometer") } returns Optional.empty()

        // WHEN
        assertThatThrownBy {
            svc.register("john doe",
                    "john.doe@hotmail.com", "+0123456789",
                    "barometer")
        }

        // THEN
        .isInstanceOf(IllegalArgumentException::class.java)
    }
}