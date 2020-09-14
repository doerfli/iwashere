package li.doerf.iwashere.controllers

import li.doerf.iwashere.DbTestHelper
import li.doerf.iwashere.LocationHelper
import li.doerf.iwashere.accounts.User
import li.doerf.iwashere.repositories.LocationRepository
import li.doerf.iwashere.utils.getLogger
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class LocationsControllerTest {

    private val logger = getLogger(javaClass)
    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var dbTestHelper: DbTestHelper
    @Autowired
    private lateinit var locationRepository: LocationRepository
    @Autowired
    private lateinit var cacheManager: CacheManager

    private lateinit var testuser: User


    // BeforeEach does not work here as WithUserDetails breaks this (https://github.com/spring-projects/spring-security/issues/6591)
    @BeforeEach
    fun setup() {
        dbTestHelper.cleanDb()
        testuser = dbTestHelper.createTestUser("test@test123.com")
        logger.info("user created $testuser")
    }

    @AfterEach
    fun evictAllCaches() {
        for (name in cacheManager.cacheNames) {
            cacheManager.getCache(name!!)!!.clear()
        }
    }

    @Test
    fun testIndexMethodIsSecured() {
        // when
        mvc.perform(MockMvcRequestBuilders.get("/locations").contentType(MediaType.APPLICATION_JSON))

        // then
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized)
    }

    @WithMockUser("test@test123.com")
    @Test
    fun exists() {
        val loc1 = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(loc1)

        // when
        mvc.perform(MockMvcRequestBuilders.get("/locations/exists/loc1").contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
    }

    @WithMockUser("test@test123.com")
    @Test
    fun existsNotFound() {
        // when
        mvc.perform(MockMvcRequestBuilders.get("/locations/exists/loc1").contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound)
    }

    @Test
    fun get() {
        val loc1 = LocationHelper.new("Location 1", "loc1", testuser)
        locationRepository.save(loc1)

        // when
        mvc.perform(MockMvcRequestBuilders.get("/locations/byShortname/loc1").contentType(MediaType.APPLICATION_JSON))

        // then
        .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(loc1.id?.toInt())))
            .andExpect(jsonPath("$.name", `is`(loc1.name)))
    }

    @Test
    fun getNotFound() {
        // when
        mvc.perform(MockMvcRequestBuilders.get("/locations/byShortname/loc1").contentType(MediaType.APPLICATION_JSON))

                // then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest)
    }

}