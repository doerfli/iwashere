package li.doerf.iwashere.services

import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.entities.Guest
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.GuestRepository
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.repositories.VisitRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@ActiveProfiles("test")
class VisitServiceIntTest {

    private lateinit var location: Location
    private lateinit var testuser: User

    @Autowired
    private lateinit var visitService: VisitService
    @Autowired
    private lateinit var visitRepository: VisitRepository
    @Autowired
    private lateinit var guestRepository: GuestRepository
    @Autowired
    private lateinit var locationRepository: LocationRepository
    @Autowired
    private lateinit var dbTestHelper: DbTestHelper

    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
        testuser = dbTestHelper.createTestUser("test@test123.com")
        location = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(location)
    }

    @Test
    fun clean() {
        val visitor1 = guestRepository.save(Guest(
                null,
                "first name",
                "first@mail.com",
                "1234567890"
        ))
        visitRepository.save(Visit(
                null,
                visitor1,
                location,
                Instant.now()
        ))

        val visitor2 = guestRepository.save(Guest(
                null,
                "Joe Second",
                "first@mail.com",
                "1234567890"
        ))
        visitRepository.save(Visit(
                null,
                visitor2,
                location,
                Instant.now().minus(35, ChronoUnit.DAYS)
        ))

        visitService.cleanup(28)

        assertThat(visitRepository.count()).isEqualTo(1)
        assertThat(guestRepository.count()).isEqualTo(1)
        assertThat(visitRepository.findAll().map { it.guest.name }).contains("first name")
    }
}