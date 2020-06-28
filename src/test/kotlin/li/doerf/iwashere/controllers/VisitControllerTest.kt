package li.doerf.iwashere.controllers

import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.TestHelper
import li.doerf.iwashere.dto.visit.VisitRegisterRequest
import li.doerf.iwashere.entities.User
import li.doerf.iwashere.repositories.LocationRepository
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class VisitControllerTest {

    private lateinit var testuser: User

    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var dbTestHelper: DbTestHelper
    @Autowired
    private lateinit var testHelper: TestHelper
    @Autowired
    private lateinit var locationRepository: LocationRepository

    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
        testuser = dbTestHelper.createTestUser("test@test123.com")
    }

    @Test
    fun register() {
        val loc1 = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(loc1)

        // when
        mvc.perform(MockMvcRequestBuilders.post("/visits")
                .content(testHelper.asJsonString(
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
}