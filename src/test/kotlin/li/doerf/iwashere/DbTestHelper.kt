package li.doerf.iwashere

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.repositories.UserRepository
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.repositories.VisitorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DbTestHelper {

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var locationRepository: LocationRepository
    @Autowired
    private lateinit var visitorRepository: VisitorRepository
    @Autowired
    private lateinit var visitRepository: VisitRepository

    fun cleanDb() {
        visitRepository.deleteAll()
        visitorRepository.deleteAll()
        locationRepository.deleteAll()
        userRepository.deleteAll()
    }

    fun createTestUser(username: String): User {
        return userRepository.save(User(null, username, "xxx"))
    }

}