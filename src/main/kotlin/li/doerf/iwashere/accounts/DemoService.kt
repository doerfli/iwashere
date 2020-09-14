package li.doerf.iwashere.accounts

import com.github.javafaker.Faker
import li.doerf.iwashere.guests.GuestsService
import li.doerf.iwashere.locations.Location
import li.doerf.iwashere.locations.LocationRepository
import li.doerf.iwashere.locations.LocationsCommandService
import li.doerf.iwashere.locations.LocationsService
import li.doerf.iwashere.utils.getLogger
import li.doerf.iwashere.visits.VisitRepository
import li.doerf.iwashere.visits.VisitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Service
@Transactional
class DemoService @Autowired constructor(
        private val userRepository: UserRepository,
        private val locationsService: LocationsService,
        private val locationRepository: LocationRepository,
        private val locationsCommandService: LocationsCommandService,
        private val visitService: VisitService,
        private val visitRepository: VisitRepository,
        private val guestsService: GuestsService,
        private val demoDeleteService: DemoDeleteService
) {

    private val log = getLogger(this::class.java)

    suspend fun resetDemoAccount() {
        log.info("resetting demo user account")
        val user = userRepository.findFirstByUsername(USERNAME_DEMO_ACCOUNT).orElseThrow{ IllegalStateException("demo user not found") }
        dropAllDemoLocations(user)
        createDemoLocations(user)
    }

    fun dropAllDemoLocations(user: User) {
        val locations = locationsService.getAll(user)
        locations.forEach {
            log.info("purging demo location: ${it.name}")
            val visits = visitService.getAllByLocation(it.shortname, user)
            val guests = visits.map { visit -> visit.guest }
            visitRepository.deleteAll(visits)
            guestsService.deleteAll(guests)
        }
        visitRepository.flush()
        locations.forEach {
            demoDeleteService.deleteLocation(it)
        }
        locationRepository.flush()

    }

    private suspend fun createDemoLocations(user: User) {
        createLocation("Gasthaus Sonne", "demo_gasthaus_sonne", "Landweg 42", "9876", "Tief im Tal", "Schweiz", user)
        createLocation("Gasthof zum Ochsen", "demo_ochsen", "Weiherweg 3", "9667", "Oberwilkon", "Schweiz", user)
    }

    private suspend fun createLocation(name: String, short: String, adr: String, plz: String, city: String, country: String, user: User) {
        val newLocation = Location(null, name, short, adr, plz, city, country, LocalDateTime.now(), user)
        val location = locationsCommandService.create(newLocation, user)
        val faker = Faker()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val dates = listOf(LocalDateTime.now(), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(3))
        dates.forEach { date ->
            repeat(Random.nextInt(10)) {
                visitService.register(
                        faker.name().name(),
                        faker.internet().emailAddress(),
                        faker.phoneNumber().phoneNumber(),
                        short,
                        fmt.format(date),
                        true
                )
            }
        }
    }

    companion object {
        private const val USERNAME_DEMO_ACCOUNT = "demo@ich-war-da.net"
    }

}