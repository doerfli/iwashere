package li.doerf.iwashere

import com.fasterxml.jackson.databind.ObjectMapper

internal class TestHelper {

    companion object {
        fun asJsonString(obj: Any): String {
            return try {
                ObjectMapper().writeValueAsString(obj)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    }

}