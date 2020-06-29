package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.services.mail.MailService
import li.doerf.iwashere.utils.getLogger
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Service
class VisitServiceImpl(
        private val visitRepository: VisitRepository,
        private val guestsService: GuestsService,
        private val locationsService: LocationsService,
        private val mailService: MailService
) : VisitService {

    private val logger = getLogger(javaClass)

    override suspend fun register(name: String, email: String, phone: String, locationShortname: String): Visit {
        logger.trace("registering visit $name, $email, $phone at $locationShortname")
        val location = locationsService.getByShortName(locationShortname)
        if (location.isEmpty) {
            throw IllegalArgumentException("location unknown $locationShortname")
        }
        val visitor = guestsService.create(name, email, phone)
        val visit = visitRepository.save(Visit(
                null,
                visitor,
                location.get()
        ))
        logger.debug("visit saved: $visit")
        logger.info("visit registered id: ${visit.id} - location: ${visit.location}")
        mailService.sendVisitMail(visit)
        return visit
    }

    override fun cleanup(retentionDays: Long) {
        logger.info("cleaning up visits older than $retentionDays days")
        val cleanupDay = LocalDateTime.now().minus(retentionDays, ChronoUnit.DAYS)
        val visits = visitRepository.findAllByRegistrationDateBefore(cleanupDay)
        val visitors = visits.map { it.guest }
        logger.info("deleting ${visits.size} visits")
        visitRepository.deleteAll(visits)
        guestsService.deleteAll(visitors)
    }

    override fun list(locationShortname: String, date: LocalDate, user: User): Collection<Visit> {
        logger.trace("retrieving all guests for location $locationShortname")
        val location = locationsService.getByShortName(locationShortname, user)
        if (location.isEmpty) {
            throw IllegalArgumentException("unknown location: $locationShortname")
        }
        val offset = ZoneId.of(ZoneId.systemDefault().id).rules.getOffset(Instant.now())
        val after = date.atStartOfDay()
        val before= date.plusDays(1).atStartOfDay()
        val visits = visitRepository.findAllByLocationAndRegistrationDateBetween(location.get(), after, before)
        // double check that location of visit belongs to current user
        return visits.filter { visit -> visit.location.user.id == user.id }
    }

}