package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(LocationRepository::class)
internal class LocationsServiceImplTest {

    private lateinit var user: User
    private lateinit var user2: User
    private lateinit var svc: LocationsServiceImpl

    @MockkBean
    private lateinit var locationRepository: LocationRepository

    @BeforeEach
    fun setup() {
        svc = LocationsServiceImpl(locationRepository)
        user = mockkClass(User::class)
        user2 = mockkClass(User::class)
    }

    @Test
    fun create() {
        // GIVEN
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user)
        val newLocSaved = Location(42, "Location 1", "loc1", null, null, null, null, user)

        every { locationRepository.countFirstByShortnameAndUser("loc1", any()) } returns 0
        every { locationRepository.save(newLoc) } returns newLocSaved

        // WHEN
        val loc = svc.create(newLoc, user)

        // THEN
        assertThat(loc).isEqualTo(newLocSaved)
    }

    @Test
    fun createAlreadyExists() {
        // GIVEN
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user)

        every { locationRepository.countFirstByShortnameAndUser("loc1", any()) } returns 1

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
        val newLoc = Location(42, "Location 1", "loc1", null, null, null, null, user)

        every { locationRepository.countFirstByShortnameAndUser("loc1", any()) } returns 0

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
        val newLoc = Location(null, "Location 1", "loc1", null, null, null, null, user2)

        every { locationRepository.countFirstByShortnameAndUser("loc1", any()) } returns 0

        // WHEN
        val loc =

                // THEN
                assertThatThrownBy {
                    svc.create(newLoc, user)
                }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun exists() {
        every { locationRepository.countFirstByShortnameAndUser("loc1", any()) } returns 0
        every { locationRepository.countFirstByShortnameAndUser("loc2", any()) } returns 1

        assertThat(svc.exists("loc1", user)).isFalse()
        assertThat(svc.exists("loc2", user)).isTrue()
    }

    @Test
    fun getAll() {
        val loc1 = LocationHelper.new("Location 1", "loc1", user)
        val loc2 = LocationHelper.new("Location 2", "loc2", user)

        every { locationRepository.getAllByUser(any()) } returns listOf(loc1, loc2)

        val list = svc.getAll(user)

        assertThat(list)
                .contains(loc1)
                .contains(loc2)
    }
}