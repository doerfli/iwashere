package li.doerf.iwashere

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
internal class TestHelper {

    fun asJsonString(obj: Any): String {
        return try {
            ObjectMapper().writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }



}