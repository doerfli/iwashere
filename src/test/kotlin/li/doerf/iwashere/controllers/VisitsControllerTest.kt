package li.doerf.iwashere.controllers

import com.ninjasquad.springmockk.MockkBean
import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.TestHelper
import li.doerf.iwashere.dto.visit.VisitRegisterRequest
import li.doerf.iwashere.entities.Guest
import li.doerf.iwashere.entities.Location
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.GuestRepository
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.services.mail.MailService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class VisitsControllerTest {

    private lateinit var location: Location
    private lateinit var testuser: User

    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var dbTestHelper: DbTestHelper
    @Autowired
    private lateinit var locationRepository: LocationRepository
    @Autowired
    private lateinit var guestRepository: GuestRepository
    @Autowired
    private lateinit var visitRepository: VisitRepository
    @MockkBean(relaxed = true)
    private lateinit var mailService: MailService

    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
        testuser = dbTestHelper.createTestUser("test@test123.com")
        location = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(location)
    }

    @Test
    fun register() {

        // when
        mvc.perform(MockMvcRequestBuilders.post("/visits")
                .content(TestHelper.asJsonString(
                        VisitRegisterRequest(
                                "loc1",
                                "John Doe",
                                "john@doe.com",
                                "+41798654321"
                        )
                ))
                .contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", CoreMatchers.notNullValue()))

    }

    @Test
    fun listProtected() {
        // when
        mvc.perform(MockMvcRequestBuilders.get("/visits/loc1/2020-03-03").contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @WithMockUser("test@test123.com")
    @Test
    fun list() {
        createVisit("1")
        createVisit("2", dateTime = LocalDateTime.now().minus(1, ChronoUnit.DAYS))
        createVisit("3", dateTime = LocalDateTime.now().minus(2, ChronoUnit.DAYS))
        createVisit("4")

        val date = SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now()))

        // when
        mvc.perform(MockMvcRequestBuilders.get("/visits/loc1/$date").contentType(MediaType.APPLICATION_JSON))
                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[0].guest_name", CoreMatchers.equalTo("name1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[0].guest_email", CoreMatchers.equalTo("name1@mail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[0].guest_phone", CoreMatchers.equalTo("0000001")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[0].visitTimestamp", CoreMatchers.equalTo(date)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visits[1].guest_name", CoreMatchers.equalTo("name4")))

    }

    @Test
    fun verifyEmail() {
        val visit = createVisit("1")

        // when
        mvc.perform(MockMvcRequestBuilders.put("/visits/${visit.id}/verify/email").contentType(MediaType.APPLICATION_JSON))
                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)


        val resultObj = visitRepository.findById(visit.id!!).get()
        assertThat(resultObj.verifiedEmail).isTrue
    }

    @Test
    fun verifyPhone() {
        val visit = createVisit("1")

        // when
        mvc.perform(MockMvcRequestBuilders.put("/visits/${visit.id}/verify/phone").contentType(MediaType.APPLICATION_JSON))
                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)


        val resultObj = visitRepository.findById(visit.id!!).get()
        assertThat(resultObj.verifiedPhone).isTrue
    }

    private fun createVisit(id: String, dateTime: LocalDateTime = LocalDateTime.now(), loc: Location = location): Visit {
        val visitor1 = guestRepository.save(Guest(
                null,
                "name$id",
                "name$id@mail.com",
                "000000$id"
        ))
        return visitRepository.save(Visit(
                null,
                visitor1,
                loc,
                dateTime
        ))
    }
}