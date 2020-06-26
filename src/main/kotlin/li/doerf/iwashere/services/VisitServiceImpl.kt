package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class VisitServiceImpl(
        private val visitRepository: VisitRepository,
        private val visitorService: VisitorService,
        private val locationsService: LocationsService
) : VisitService {

    private val logger = getLogger(javaClass)

    override fun register(firstname: String, lastname: String, email: String, phone: String, locationShortname: String): Visit {
        logger.trace("registering visit $firstname $lastname, $email, $phone at $locationShortname")
        val location = locationsService.getByShortName(locationShortname)
        if (location.isEmpty) {
            throw IllegalArgumentException("location unknown $locationShortname")
        }
        val visitor = visitorService.createVisitor(firstname, lastname, email, phone)
        val visit = visitRepository.save(Visit(
                null,
                visitor,
                location.get(),
                Instant.now()
        ))
        logger.debug("visit saved: $visit")
        logger.info("visit registered id: ${visit.id} - location: ${visit.location}")
        return visit
    }

    override fun cleanup(retentionDays: Long) {
        logger.info("cleaning up visits older than $retentionDays days")
        val cleanupDay = Instant.now().minus(retentionDays, ChronoUnit.DAYS)
        val visits = visitRepository.findAllByRegistrationTimeBefore(cleanupDay)
        val visitors = visits.map { it.visitor }
        logger.info("deleting ${visits.size} visits")
        visitRepository.deleteAll(visits)
        visitorService.deleteAll(visitors)
    }

}