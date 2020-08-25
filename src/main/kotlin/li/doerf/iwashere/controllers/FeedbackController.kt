package li.doerf.iwashere.controllers

import li.doerf.iwashere.dto.FeedbackRequestDto
import li.doerf.iwashere.services.FeedbackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("feedback")
class FeedbackController @Autowired constructor(
        private val feedbackService: FeedbackService
) {

    @PostMapping
    fun postFeedback(@RequestBody request: FeedbackRequestDto): HttpStatus {
        feedbackService.send(request.name, request.email, request.message)
        return HttpStatus.OK
    }

}