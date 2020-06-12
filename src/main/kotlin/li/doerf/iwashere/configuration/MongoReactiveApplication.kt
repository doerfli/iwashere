package li.doerf.subscribed.configuration

import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories
class MongoReactiveApplication(
        private val mongoDatabase: String
) : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName(): String {
        return mongoDatabase
    }

}