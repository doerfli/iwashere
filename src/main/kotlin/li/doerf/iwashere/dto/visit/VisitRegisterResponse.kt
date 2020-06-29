package li.doerf.iwashere.dto.visit

import java.time.LocalDateTime

data class VisitRegisterResponse(
        val id: Long,
        val timestamp: LocalDateTime
)
