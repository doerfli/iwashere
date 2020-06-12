package li.doerf.iwashere

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IwashereApplication

fun main(args: Array<String>) {
	dotenv {
		systemProperties = true
	}
	runApplication<IwashereApplication>(*args)
}
