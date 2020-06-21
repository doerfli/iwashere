package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Visitor
import li.doerf.iwashere.repositories.VisitorRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service

@Service
class VisitorServiceImpl(
        private val visitorRepository: VisitorRepository
) : VisitorService {

    private val logger = getLogger(javaClass)

    override fun createVisitor(firstname: String, lastname: String, email: String, phone: String): Visitor {
        logger.trace("creating visitor $firstname $lastname, $email, $phone")
        val visitor = visitorRepository.save(Visitor(
                null,
                firstname,
                lastname,
                email,
                phone
        ))
        logger.debug("visitor saved: $visitor")
        return visitor
    }


}