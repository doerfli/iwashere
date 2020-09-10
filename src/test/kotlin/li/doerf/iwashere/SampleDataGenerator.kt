package li.doerf.iwashere

import com.github.javafaker.Faker
import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.account.SignupRequest
import li.doerf.iwashere.dto.location.LocationCreateRequest
import li.doerf.iwashere.dto.visit.VisitRegisterRequest
import li.doerf.iwashere.utils.now
import java.time.format.DateTimeFormatter
import kotlin.random.Random

val apiBaseUrl = "http://localhost:8080/api"

fun main() {
    val faker = Faker()
    runBlocking {
        val client1 = registerUser("marc@doerf.li", "11111111")
        repeat(10) {
            val shortname = createLocation(client1, faker)
            registerGuests(client1, faker, shortname)
        }

        val client2 = registerUser("marc@bytes.li", "11111111")
        repeat(10) {
            val shortname = createLocation(client2, faker)
            registerGuests(client2, faker, shortname)
        }
    }
}

suspend fun registerGuests(client: HttpClient, faker: Faker, shortname: String) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    repeat(30) { i ->
        val amount = Random.nextInt(30)
        val date = now().minusDays(i.toLong())
        repeat(amount) {
            client.post<String>("$apiBaseUrl/visits") {
                header("content-type", "application/json")
                body = TestHelper.asJsonString(VisitRegisterRequest(
                        shortname,
                        faker.name().name(),
                        faker.internet().emailAddress(),
                        faker.phoneNumber().phoneNumber(),
                        fmt.format(date)
                ))
            }

        }
    }
}


suspend fun createLocation(client: HttpClient, faker: Faker): String {
    val name = faker.company().name()
    val shortname = if (name.contains(" ")) {
        name.substring(0, name.indexOf(" ")).toLowerCase()
    } else {
        name.toLowerCase()
    }
    client.post<String>("$apiBaseUrl/locations") {
        header("content-type", "application/json")
        body = TestHelper.asJsonString(LocationCreateRequest(
                name,
                shortname,
                faker.address().streetAddress(),
                faker.address().zipCode(),
                faker.address().city(),
                faker.address().country()
        ))
    }
    return shortname
}

suspend fun registerUser(username: String, password: String) : HttpClient {
    val client = HttpClient() {
        install(HttpCookies) {
            // Will keep an in-memory map with all the cookies from previous requests.
            storage = AcceptAllCookiesStorage()
        }
    }
    client.post<String>("$apiBaseUrl/accounts/signup") {
        header("content-type", "application/json")
        body = TestHelper.asJsonString(SignupRequest(username, password))
    }
    client.post<HttpResponse>("$apiBaseUrl/login") {
        body = FormDataContent(Parameters.build {
            append("username", username)
            append("password", password)
        })
    }

    return client
}
