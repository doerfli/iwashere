package li.doerf.iwashere

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DbTestHelper {

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var locationRepository: LocationRepository

    fun cleanDb() {
        userRepository.deleteAll()
        locationRepository.deleteAll()
    }

    fun createTestUser(username: String): User {
        return userRepository.save(User(null, username, "xxx"))
    }

}