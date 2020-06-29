package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Guest
import li.doerf.iwashere.repositories.GuestRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service

@Service
class GuestsServiceImpl(
        private val guestRepository: GuestRepository
) : GuestsService {

    private val logger = getLogger(javaClass)

    override fun create(name: String, email: String, phone: String): Guest {
        logger.trace("creating visitor $name, $email, $phone")
        val visitor = guestRepository.save(Guest(
                null,
                name,
                email,
                phone
        ))
        logger.debug("visitor saved: $visitor")
        return visitor
    }

    override fun deleteAll(guests: List<Guest>) {
        logger.info("deleting ${guests.size} visitors")
        guestRepository.deleteAll(guests)
    }


}