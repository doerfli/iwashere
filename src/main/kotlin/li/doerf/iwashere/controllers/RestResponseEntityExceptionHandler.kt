package li.doerf.iwashere.controllers

import li.doerf.iwashere.services.ExpiredTokenException
import li.doerf.iwashere.services.InvalidUserStateException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [IllegalArgumentException::class])
    protected fun handleIllegalArgumentException(
            ex: IllegalArgumentException, request: WebRequest): ResponseEntity<Any?>? {
        val bodyOfResponse = ex.message
        return handleExceptionInternal(ex, bodyOfResponse,
                HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(value = [InvalidUserStateException::class])
    protected fun handleInvalidUserState(
            ex: InvalidUserStateException, request: WebRequest): ResponseEntity<Any?>? {
        val bodyOfResponse = ex.message
        return handleExceptionInternal(ex, bodyOfResponse,
                HttpHeaders(), HttpStatus.CONFLICT, request)
    }

    @ExceptionHandler(value = [ExpiredTokenException::class])
    protected fun handleExpiredToken(
            ex: ExpiredTokenException, request: WebRequest): ResponseEntity<Any?>? {
        val bodyOfResponse = ex.message
        return handleExceptionInternal(ex, bodyOfResponse,
                HttpHeaders(), HttpStatus.UNAUTHORIZED, request)
    }

}