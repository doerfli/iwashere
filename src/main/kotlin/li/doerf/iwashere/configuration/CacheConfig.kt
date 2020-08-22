package li.doerf.iwashere.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer


@EnableCaching
@Configuration
class CacheConfig {

    @Bean
    @Primary
    fun defaultCacheConfig(objectMapper: ObjectMapper): RedisCacheConfiguration? {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer<Any>(GenericJackson2JsonRedisSerializer(objectMapper)))
    }

}