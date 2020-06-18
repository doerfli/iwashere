package li.doerf.iwashere.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint


@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
                .authorizeRequests()
                .antMatchers("/home/**","/login","/accounts/signup","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
            .and()
                .csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler { _, response, _ ->
                    response.status = 200
                }
                .failureHandler { _, response, _ ->
                    response.status = 401
                }
            .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}