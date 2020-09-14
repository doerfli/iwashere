package li.doerf.iwashere.common.feedback

data class FeedbackRequestDto(
        val name: String,
        val email: String,
        val message: String
)
