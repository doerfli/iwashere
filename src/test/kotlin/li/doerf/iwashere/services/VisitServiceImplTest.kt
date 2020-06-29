package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.entities.Guest
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.services.mail.MailService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@Import(VisitRepository::class, GuestsService::class, LocationsService::class)
internal class VisitServiceImplTest {

    private lateinit var svc: VisitServiceImpl

    @MockkBean
    private lateinit var visitRepository: VisitRepository
    @MockkBean
    private lateinit var guestsService: GuestsService
    @MockkBean
    private lateinit var locationsService: LocationsService
    @MockkBean(relaxed = true)
    private lateinit var mailService: MailService

    @BeforeEach
    fun setup() {
        svc = VisitServiceImpl(
                visitRepository,
                guestsService,
                locationsService,
                mailService
        )
    }

    @Test
    fun register() {
        // GIVEN
        val location = mockkClass(Location::class)
        every { locationsService.getByShortName("barometer") } returns Optional.of(location)
        val visitor = mockkClass(Guest::class)
        every { guestsService.create("john doe",
                "john.doe@hotmail.com", "+0123456789") } returns visitor
        val visit = Visit(
                3,
                visitor,
                location,
                LocalDateTime.now()
        )
        every { visitRepository.save(any() as Visit) } returns visit

        // WHEN
        val result = runBlocking {
            svc.register("john doe",
                    "john.doe@hotmail.com", "+0123456789",
                    "barometer")
        }

        // THEN
        assertThat(result).isEqualTo(visit)

        coVerify { mailService.sendVisitMail(any()) }
    }

    @Test
    fun registerNotFound() {
        // GIVEN
        every { locationsService.getByShortName("barometer") } returns Optional.empty()

        // WHEN
        assertThatThrownBy {
            runBlocking {
                svc.register("john doe",
                        "john.doe@hotmail.com", "+0123456789",
                        "barometer")
            }
        }

        // THEN
        .isInstanceOf(IllegalArgumentException::class.java)
    }

}