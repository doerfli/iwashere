package li.doerf.iwashere.utils

import li.doerf.iwashere.documents.User
import li.doerf.iwashere.security.UserPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.security.Principal

internal fun getUser(principal: Principal): User {
    val username = (principal as UsernamePasswordAuthenticationToken).principal as UserPrincipal
    return username.user
}
