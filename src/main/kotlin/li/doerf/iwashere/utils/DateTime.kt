package li.doerf.iwashere.utils

import java.time.Clock
import java.time.LocalDateTime

/**
 * Retrieve LocalDateTime object for now in UTC.
 */
fun now(): LocalDateTime = LocalDateTime.now(Clock.systemUTC())