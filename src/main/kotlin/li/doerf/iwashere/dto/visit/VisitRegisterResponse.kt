package li.doerf.iwashere.dto.visit

import java.time.Instant

data class VisitRegisterResponse(
        val id: Long,
        val timestamp: Instant
)
