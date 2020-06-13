package li.doerf.iwashere.configuration

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate


@Configuration
class MongoConfig {

    @Value("\${mongo.databasename}")
    private lateinit var database: String
    @Value("\${mongo.connection}")
    private lateinit var connection: String

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create(connection)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), database)
    }

    @Bean
    fun mongoDatabase(): String {
        return database
    }

}