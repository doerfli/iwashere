package li.doerf.iwashere.visits.dto

import java.time.LocalDateTime

data class VisitRegisterResponse(
        val id: Long,
        val timestamp: LocalDateTime
)
