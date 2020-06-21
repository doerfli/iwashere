package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Visit

interface VisitService {
    fun register(firstname: String, lastname: String, email: String, phone: String, locationShortname: String): Visit
}
