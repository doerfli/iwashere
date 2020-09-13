package li.doerf.iwashere.services

import li.doerf.iwashere.entities.User
import li.doerf.iwashere.entities.Visit
import li.doerf.iwashere.repositories.DateGuestcount
import li.doerf.iwashere.repositories.VisitRepository
import li.doerf.iwashere.services.mail.MailService
import li.doerf.iwashere.utils.getLogger
import li.doerf.iwashere.utils.now
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class VisitServiceImpl(
        private val visitRepository: VisitRepository,
        private val guestsService: GuestsService,
        private val locationsService: LocationsService,
        private val mailService: MailService
) : VisitService {

    private val logger = getLogger(javaClass)

    override suspend fun register(name: String, email: String, phone: String, locationShortname: String, timestampStr: String?, suppressEmail: Boolean): Visit {
        logger.trace("registering visit $name, $email, $phone at $locationShortname")
        val location = locationsService.getByShortName(locationShortname)
        if (location.isEmpty) {
            throw IllegalArgumentException("location unknown $locationShortname")
        }
        val timestamp = if (timestampStr != null) {
            LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse("$timestampStr 11:00"))
        } else {
            now()
        }
        val visitor = guestsService.create(name, email, phone)
        val visit = visitRepository.save(Visit(
                null,
                visitor,
                location.get(),
                timestamp
        ))
        logger.debug("visit saved: $visit")
        logger.info("visit registered id: ${visit.id} - location: ${visit.location}")
        if (!suppressEmail) {
            mailService.sendVisitMail(visit)
        }
        return visit
    }

    override fun cleanup(retentionDays: Long) {
        logger.info("cleaning up visits older than $retentionDays days")
        val cleanupDay = now().minus(retentionDays, ChronoUnit.DAYS)
        val visits = visitRepository.findAllByVisitTimestampBefore(cleanupDay)
        val visitors = visits.map { it.guest }
        logger.info("deleting ${visits.size} visits")
        visitRepository.deleteAll(visits)
        guestsService.deleteAll(visitors)
    }

    override fun list(locationShortname: String, date: LocalDate, user: User): Collection<Visit> {
        logger.trace("retrieving all guests for location '$locationShortname'")
        val location = locationsService.getByShortName(locationShortname, user)
        if (location.isEmpty) {
            throw IllegalArgumentException("unknown location: $locationShortname")
        }
        val after = date.atStartOfDay()
        val before= date.plusDays(1).atStartOfDay()
        val visits = visitRepository.findAllByLocationAndVisitTimestampBetween(location.get(), after, before)
        // double check that location of visit belongs to current user
        return visits.filter { visit -> visit.location.user.id == user.id }
    }

    override fun getAllByLocation(locationShortname: String, user: User): Collection<Visit> {
        logger.trace("retrieving all guests for location '$locationShortname'")
        val location = locationsService.getByShortName(locationShortname, user)
        if (location.isEmpty) {
            throw IllegalArgumentException("unknown location: $locationShortname")
        }
        val visits = visitRepository.findAllByLocation(location.get())
        // double check that location of visit belongs to current user
        return visits.filter { visit -> visit.location.user.id == user.id }
    }

    override fun listDates(locationShortname: String, user: User): Map<String, Long> {
        logger.trace("retrieving guest counts and dates for location '$locationShortname'")
        val location = locationsService.getByShortName(locationShortname, user)
        if (location.isEmpty) {
            throw IllegalArgumentException("unknown location: $locationShortname")
        }
        return visitRepository.getDateGuestCountList(location.get()).map{ fixDatePadding(it) }.map{ it.getDate() to it.getGuestcount() }.toMap()
    }

    override fun countToday(): Long {
        logger.trace("retrieving visit count for today")
        val after = LocalDate.now().atStartOfDay()
        val before = LocalDate.now().plusDays(1).atStartOfDay()
        return visitRepository.countAllByVisitTimestampBetween(after, before)
    }

    override fun verifyEmail(id: Long) {
        logger.trace("verify email for visit $id")
        val visit = visitRepository.findById(id).orElseThrow{ IllegalArgumentException("Invalid visit id: $id") }
        visit.verifiedEmail = true
        visitRepository.save(visit)
        logger.debug("visit email verified $visit")
    }

    override fun verifyPhone(id: Long) {
        logger.trace("verify phone for visit $id")
        val visit = visitRepository.findById(id).orElseThrow{ IllegalArgumentException("Invalid visit id: $id") }
        visit.verifiedPhone = true
        visitRepository.save(visit)
        logger.debug("visit phone verified $visit")
    }

    private fun fixDatePadding(dg: DateGuestcount): DateGuestcount {
        val date = dg.getDate()
        if (date.length == 10) {
            return dg
        }

        var (year, month, day) = date.split("-")
        if (month.length < 2) {
            month = month.padStart(2, '0')
        }
        if (day.length < 2) {
            day = day.padStart(2, '0')
        }
        return object : DateGuestcount{
            override fun getDate(): String {
                return "$year-$month-$day"
            }

            override fun getGuestcount(): Long {
                return dg.getGuestcount()
            }
        }
    }

}