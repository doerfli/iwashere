package li.doerf.iwashere.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkClass
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

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
        every { user.id } returns 13
        user2 = mockkClass(User::class)
        every { user2.id } returns 14
    }

    @Test
    fun exists() {
        every { locationRepository.countFirstByShortname("loc1") } returns 0
        every { locationRepository.countFirstByShortname("loc2") } returns 1

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

    @Test
    fun get() {
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)
        every { locationRepository.findFirstByShortname("loc1") } returns Optional.of(loc)

        // WHEN
        val result = svc.getByShortName("loc1")

        // THEN
        assertThat(result.isPresent).isTrue()
        assertThat(result.get().id).isEqualTo(42)
    }

    @Test
    fun getNotFound() {
        val loc = Location(42, "Location 1", "loc1", null, null, null, null, user = user)
        every { locationRepository.findFirstByShortname("loc1") } returns Optional.empty()

        // WHEN
        val result = svc.getByShortName("loc1")

        // THEN
        assertThat(result.isPresent).isFalse()
    }

}