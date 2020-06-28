package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Visit

interface VisitService {
    fun register(name: String, email: String, phone: String, locationShortname: String): Visit
    fun cleanup(retentionDays: Long): Any
}
