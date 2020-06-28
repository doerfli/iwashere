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

    override fun createVisitor(name: String, email: String, phone: String): Visitor {
        logger.trace("creating visitor $name, $email, $phone")
        val visitor = visitorRepository.save(Visitor(
                null,
                name,
                email,
                phone
        ))
        logger.debug("visitor saved: $visitor")
        return visitor
    }

    override fun deleteAll(visitors: List<Visitor>) {
        logger.info("deleting ${visitors.size} visitors")
        visitorRepository.deleteAll(visitors)
    }


}