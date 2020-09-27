package li.doerf.iwashere.guests

import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service

@Service
class GuestsServiceImpl(
        private val guestRepository: GuestRepository
) : GuestsService {

    private val logger = getLogger(javaClass)

    override fun create(name: String, email: String, phone: String, street: String?, zip: String?, city: String?, country: String?): Guest {
        logger.trace("creating visitor $name, $email, $phone")
        val visitor = guestRepository.save(Guest(
                null,
                name,
                email,
                phone,
                street,
                zip,
                city,
                country
        ))
        logger.debug("visitor saved: $visitor")
        return visitor
    }

    override fun deleteAll(guests: List<Guest>) {
        logger.info("deleting ${guests.size} visitors")
        guestRepository.deleteAll(guests)
    }


}