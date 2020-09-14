package li.doerf.iwashere.infrastructure.monitoring

import li.doerf.iwashere.visits.VisitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monitoring")
class MonitoringController(
        private val visitService: VisitService
) {

    @GetMapping
    fun check(): ResponseEntity<CheckResponse> {
        val count = visitService.countToday()
        return ResponseEntity.ok(CheckResponse(count))
    }

}