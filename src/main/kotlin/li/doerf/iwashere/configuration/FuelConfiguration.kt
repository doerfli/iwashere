package li.doerf.subscribed.configuration

import com.github.kittinunf.fuel.Fuel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FuelConfiguration {

    @Bean
    fun fuel(): Fuel {
        return Fuel
    }
}