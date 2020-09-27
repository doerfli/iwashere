package li.doerf.iwashere.guests

interface GuestsService {
    fun create(name: String, email: String, phone: String, street: String?, zip: String?, city: String?, country: String?): Guest
    fun deleteAll(guests: List<Guest>)
}
