package li.doerf.iwashere

import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.accounts.UserRepository
import li.doerf.iwashere.locations.LocationRepository
import li.doerf.iwashere.repositories.GuestRepository
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.now
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DbTestHelper {

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var locationRepository: LocationRepository
    @Autowired
    private lateinit var guestRepository: GuestRepository
    @Autowired
    private lateinit var visitRepository: VisitRepository

    fun cleanDb() {
        visitRepository.deleteAll()
        guestRepository.deleteAll()
        locationRepository.deleteAll()
        userRepository.deleteAll()
    }

    fun createTestUser(username: String): User {
        return userRepository.save(User(null, username, "xxx", token = UserHelper.generateToken(), tokenValidUntil = now().plusMinutes(5)))
    }

}