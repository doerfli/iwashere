package li.doerf.iwashere.guests

interface GuestsService {
    fun create(name: String, email: String, phone: String): Guest
    fun deleteAll(guests: List<Guest>)
}
