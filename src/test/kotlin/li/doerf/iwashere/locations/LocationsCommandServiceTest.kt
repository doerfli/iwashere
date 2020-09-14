package li.doerf.iwashere.locations

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.locations.dto.toLocationDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@Import(LocationsServiceImpl::class, LocationRepository::class)
internal class LocationsCommandServiceTest {

    private lateinit var user: User
    private lateinit var user2: User
    private lateinit var svc: LocationsCommandService

    @Autowired
    private lateinit var locationsService: LocationsService
    @MockkBean
    private lateinit var locationRepository: LocationRepository

    @BeforeEach
    fun setup() {
        svc = LocationsCommandService(locationsService, locationRepository)
        user = mockkClass(User::class)
        every { user.id } returns 13
        user2 = mockkClass(User::class)
        every { user2.id } returns 14
    }

    @Test
    fun create() {
        // GIVEN
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user = user)
        val newLocSaved = Location(42, "Location 1", "loc1", null, null, null, null, user = user)

        every { locationRepository.countFirstByShortname("loc1") } returns 0
        every { locationRepository.save(newLoc) } returns newLocSaved

        // WHEN
        val loc = svc.create(newLoc, user)

        // THEN
        assertThat(loc).isEqualTo(newLocSaved)
    }

    @Test
    fun createAlreadyExists() {
        // GIVEN
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user = user)

        every { locationRepository.countFirstByShortname("loc1") } returns 1

        // WHEN
        val loc =

        // THEN
        assertThatThrownBy {
            svc.create(newLoc, user)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun createLocationWithId() {
        // GIVEN
        val newLoc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)

        every { locationRepository.countFirstByShortname("loc1") } returns 0

        // WHEN
        val loc =

                // THEN
                assertThatThrownBy {
                    svc.create(newLoc, user)
                }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun createLocationWithOtherUser() {
        // GIVEN
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user = user2)

        every { locationRepository.countFirstByShortname("loc1") } returns 0

        // WHEN
        val loc =

                // THEN
                assertThatThrownBy {
                    svc.create(newLoc, user)
                }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun update() {
        // GIVEN
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)
        val updateLoc = Location(42, "Location A", "locA", "strt", "zp", "cty", "cntry", user = user)

        every { locationRepository.findById(42) } returns Optional.of(loc)
        every { locationRepository.save(any() as Location) } returns updateLoc

        // WHEN
        val res = svc.update(updateLoc.toLocationDto(), user)

        // THEN
        assertThat(res).isEqualTo(updateLoc)
    }

    @Test
    fun updateNotAllowed() {
        // GIVEN
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)

        every { locationRepository.findById(42) } returns Optional.of(loc)

        // WHEN
        assertThatThrownBy {
            svc.updateShortname(42, "locA", user2)
        }.isInstanceOf(IllegalArgumentException::class.java)

        verify(exactly = 0) {
            locationRepository.save(any() as Location)
        }
    }

    @Test
    fun updateShortname() {
        // GIVEN
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)
        val updateLoc = Location(42, "Location A", "locA", null, null, null, null, user = user)

        every { locationRepository.findById(42) } returns Optional.of(loc)
        every { locationRepository.countFirstByShortname("locA") } returns 0
        every { locationRepository.save(any() as Location) } returns updateLoc

        // WHEN
        val res = svc.updateShortname(42, "locA", user)

        // THEN
        assertThat(res).isEqualTo(updateLoc)
    }

    @Test
    fun updateShortnameExists() {
        // GIVEN
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)
        val updateLoc = Location(42, "Location A", "locA", null, null, null, null, user = user)

        every { locationRepository.findById(42) } returns Optional.of(loc)
        every { locationRepository.countFirstByShortname("locA") } returns 1
//        every { locationRepository.save(any() as Location) } returns updateLoc

        // WHEN
        assertThatThrownBy {
            svc.updateShortname(42, "locA", user)
        }.isInstanceOf(IllegalArgumentException::class.java)

        verify(exactly = 0) {
            locationRepository.save(any() as Location)
        }
    }

    @Test
    fun updateShortnameNotAllowed() {
        // GIVEN
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)

        every { locationRepository.findById(42) } returns Optional.of(loc)

        // WHEN
        assertThatThrownBy {
            svc.updateShortname(42, "locA", user2)
        }.isInstanceOf(IllegalArgumentException::class.java)

        verify(exactly = 0) {
            locationRepository.save(any() as Location)
        }
    }

}