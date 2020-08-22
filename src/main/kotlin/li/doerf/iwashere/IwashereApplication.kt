package li.doerf.iwashere

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class IwashereApplication

fun main(args: Array<String>) {
	dotenv {
		systemProperties = true
		ignoreIfMissing = true
	}
	runApplication<IwashereApplication>(*args)
}
