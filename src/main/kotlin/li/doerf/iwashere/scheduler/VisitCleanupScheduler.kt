package li.doerf.iwashere.scheduler

import li.doerf.iwashere.services.VisitService
import li.doerf.iwashere.services.VisitorService
import li.doerf.iwashere.utils.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class VisitCleanupScheduler(
        private val visitService: VisitService,
        private val visitorService: VisitorService
) {

    private val logger = getLogger(javaClass)
    @Value("\${cleanup.retentionDays}")
    private var retentionDays: Long = 28

    @Scheduled(cron = "\${cleanup.cron}")
    fun cleanupVisits() {
        logger.info("Cleanup visitors after $retentionDays days")
        visitService.cleanup(retentionDays)
    }

}