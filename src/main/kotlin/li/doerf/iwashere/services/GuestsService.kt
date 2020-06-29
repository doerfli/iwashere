package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Guest

interface GuestsService {
    fun create(name: String, email: String, phone: String): Guest
    fun deleteAll(guests: List<Guest>)
}
