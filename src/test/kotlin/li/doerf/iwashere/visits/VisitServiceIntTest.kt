package li.doerf.iwashere.visits

import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.guests.Guest
import li.doerf.iwashere.guests.GuestRepository
import li.doerf.iwashere.locations.Location
import li.doerf.iwashere.locations.LocationRepository
import li.doerf.iwashere.utils.now
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
        testuser = dbTestHelper.createTestUser("test@test123.com")
        location = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(location)
    }

    @AfterEach
    fun evictAllCaches() {
        for (name in cacheManager.cacheNames) {
            cacheManager.getCache(name!!)!!.clear()
        }
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
                now()
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
                now().minus(35, ChronoUnit.DAYS)
        ))

        visitService.cleanup(28)

        assertThat(visitRepository.count()).isEqualTo(1)
        assertThat(guestRepository.count()).isEqualTo(1)
        assertThat(visitRepository.findAll().map { it.guest.name }).contains("first name")
    }

    @Test
    fun list() {
        // GIVEN
        createVisit("1")
        createVisit("2")
        createVisit("3")
        createVisit("4")
        createVisit("5")

        val today = LocalDate.now()

        // WHEN
        val visits = visitService.list(location.shortname, today, testuser)

        // THEN
        val visitNames = visits.map { it.guest.name }.toList()
        assertThat(visitNames).contains("name1").contains("name2").contains("name3").contains("name4").contains("name5")
    }

    @Test
    fun listDifferentDates() {
        // GIVEN
        createVisit("1")
        createVisit("2", dateTime = now().minus(1, ChronoUnit.DAYS))
        createVisit("3", dateTime = now().minus(2, ChronoUnit.DAYS))
        createVisit("4")
        createVisit("5", dateTime = now().minus(38, ChronoUnit.DAYS))

        val today = LocalDate.now()

        // WHEN
        val visitsToday = visitService.list(location.shortname, today, testuser)
        val visitsYesterday = visitService.list(location.shortname, today.minusDays(1), testuser)

        // THEN
        val visitNames = visitsToday.map { it.guest.name }
        assertThat(visitNames).contains("name1").doesNotContain("name2").doesNotContain("name3").contains("name4").doesNotContain("name5")
        assertThat(visitsYesterday.map { it.guest.name }).doesNotContain("name1").contains("name2").doesNotContain("name3").doesNotContain("name4").doesNotContain("name5")
    }


    private fun createVisit(id: String, dateTime: LocalDateTime = now(), loc: Location = location): Visit {
        val visitor1 = guestRepository.save(Guest(
                null,
                "name$id",
                "name$id@mail.com",
                "000000$id"
        ))
        return visitRepository.save(Visit(
                null,
                visitor1,
                loc,
                dateTime
        ))
    }

    @Test
    fun listDates() {
        // GIVEN
        val today = now()
        val yesterDay = today.minus(1, ChronoUnit.DAYS)
        val twoDaysAgo = today.minus(2, ChronoUnit.DAYS)
        val aWeekAgo = today.minus(7, ChronoUnit.DAYS)
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        createVisit("1", dateTime = today)
        createVisit("21", dateTime = yesterDay)
        createVisit("22", dateTime = yesterDay)
        createVisit("31", dateTime = twoDaysAgo)
        createVisit("32", dateTime = twoDaysAgo)
        createVisit("33", dateTime = twoDaysAgo)
        createVisit("4", dateTime = today)
        createVisit("51", dateTime = aWeekAgo)

        val result = visitService.listDates(location.shortname, testuser)

        assertThat(result[fmt.format(today)]).isEqualTo(2)
        assertThat(result[fmt.format(yesterDay)]).isEqualTo(2)
        assertThat(result[fmt.format(twoDaysAgo)]).isEqualTo(3)
        assertThat(result[fmt.format(aWeekAgo)]).isEqualTo(1)
    }

    @Test
    fun verifyPhone() {
        val visit = createVisit("1")

        visitService.verifyPhone(visit.id!!)

        val result = visitRepository.findById(visit.id!!).get()
        assertThat(result.verifiedPhone).isTrue
    }

    @Test
    fun verifyPhoneNotExists() {
        assertThatThrownBy {
            visitService.verifyPhone(1L)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun verifyEmail() {
        val visit = createVisit("1")

        visitService.verifyEmail(visit.id!!)

        val result = visitRepository.findById(visit.id!!).get()
        assertThat(result.verifiedEmail).isTrue
    }

    @Test
    fun verifyEmailNotExists() {
        assertThatThrownBy {
            visitService.verifyEmail(1L)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}