package li.doerf.subscribed.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate


@Configuration
class ReactiveMongoConfig {

    @Value("\${mongo.databasename}")
    private lateinit var database: String
    @Value("\${mongo.connection}")
    private lateinit var connection: String

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create(connection)
    }

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(mongoClient(), database)
    }

    @Bean
    fun mongoDatabase(): String {
        return database
    }

}