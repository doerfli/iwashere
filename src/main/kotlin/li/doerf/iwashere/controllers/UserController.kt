package li.doerf.iwashere.controllers

import li.doerf.iwashere.dto.user.UserGetResponse
import li.doerf.iwashere.utils.UserHelper
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("user")
class UserController(
        private val userHelper: UserHelper
) {

    @GetMapping
    fun get(principal: Principal): ResponseEntity<UserGetResponse> {
        val user = userHelper.getUser(principal)
        return ok(UserGetResponse(user.username))
    }

}