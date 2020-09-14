package li.doerf.iwashere.visits

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.guests.Guest
import li.doerf.iwashere.guests.GuestsService
import li.doerf.iwashere.infrastructure.mail.MailService
import li.doerf.iwashere.locations.Location
import li.doerf.iwashere.locations.LocationsService
import li.doerf.iwashere.utils.now
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
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
                now()
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