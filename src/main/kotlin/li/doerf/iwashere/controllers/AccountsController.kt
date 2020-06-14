package li.doerf.iwashere.controllers

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.SignupRequest
import li.doerf.iwashere.dto.SignupResponse
import li.doerf.iwashere.services.AccountsService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("accounts")
class AccountsController(
    private val accountsService: AccountsService
) {

//    private val logger = getLogger(javaClass)

    @PostMapping("signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        val user = runBlocking {
            accountsService.create(request.username, request.password)
        }
        return ok(SignupResponse(user.username, user.id))
    }

}