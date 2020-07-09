package li.doerf.iwashere.controllers

import kotlinx.coroutines.runBlocking
import li.doerf.iwashere.dto.account.ChangePasswordRequest
import li.doerf.iwashere.dto.account.SignupRequest
import li.doerf.iwashere.dto.account.SignupResponse
import li.doerf.iwashere.services.AccountsService
import li.doerf.iwashere.utils.UserHelper
import li.doerf.iwashere.utils.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.transaction.Transactional

@RestController
@RequestMapping("accounts")
@Transactional
class AccountsController(
    private val accountsService: AccountsService,
    private val userHelper: UserHelper
) {
    private val logger = getLogger(javaClass)

    @PostMapping("signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        val user = runBlocking {
            accountsService.create(request.username, request.password)
        }
        return ok(SignupResponse(user.username))
    }

    @PostMapping("confirm/{token}")
    fun confirm(@PathVariable token: String): HttpStatus {
        try {
            accountsService.confirm(token)
        } catch (e: IllegalArgumentException) {
            logger.warn("caught IllegalArgumentException", e)
        } catch (e: IllegalStateException) {
            logger.warn("caught IllegalStateException", e)
        }
        return HttpStatus.OK
    }

    @PostMapping("changePassword")
    fun changePassword(@RequestBody request: ChangePasswordRequest, principal: Principal): HttpStatus {
        logger.debug("received change password request")
        accountsService.changePassword(request.oldPassword, request.newPassword, userHelper.getUser(principal).username)
        return HttpStatus.OK
    }

}