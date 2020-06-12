package li.doerf.iwashere.repositories

import li.doerf.iwashere.documents.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono


interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findFirstByUsername(username: Mono<String>): Mono<User>
}