package li.doerf.iwashere.scheduler

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.services.DemoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DemoUserCleanupScheduler @Autowired constructor(
        val demoService: DemoService
){

    @Scheduled(cron = "\${cleanup.demoCron}")
    fun cleanup() {
        runBlocking {
            demoService.resetDemoAccount()
        }
    }

}