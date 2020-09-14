package li.doerf.iwashere.infrastructure.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.StringTemplateResolver
import java.util.*


@Configuration
class MailConfig {

    @Bean
    fun emailMessageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("mail/MailMessages")
        return messageSource
    }

    @Bean
    fun emailTemplateEngine(): TemplateEngine {
        val templateEngine = SpringTemplateEngine()
        // Resolver for TEXT emails
        templateEngine.addTemplateResolver(textTemplateResolver())
        // Resolver for HTML emails (except the editable one)
        templateEngine.addTemplateResolver(htmlTemplateResolver())
        // Resolver for HTML editable emails (which will be treated as a String)
        templateEngine.addTemplateResolver(stringTemplateResolver())
        // Message source, internationalization specific to emails
        templateEngine.setTemplateEngineMessageSource(emailMessageSource())
        return templateEngine
    }

    private fun textTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = Integer.valueOf(1)
        templateResolver.resolvablePatterns = Collections.singleton("text/*")
        templateResolver.suffix = ".txt"
        templateResolver.templateMode = TemplateMode.TEXT
        templateResolver.characterEncoding = Charsets.ISO_8859_1.name()
        templateResolver.isCacheable = false
        return templateResolver
    }

    private fun htmlTemplateResolver(): ITemplateResolver {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.order = Integer.valueOf(2)
        templateResolver.resolvablePatterns = Collections.singleton("html/*")
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = Charsets.ISO_8859_1.name()
        templateResolver.isCacheable = false
        return templateResolver
    }

    private fun stringTemplateResolver(): ITemplateResolver {
        val templateResolver = StringTemplateResolver()
        templateResolver.order = Integer.valueOf(3)
        // No resolvable pattern, will simply process as a String template everything not previously matched
        templateResolver.setTemplateMode("HTML5")
        templateResolver.isCacheable = false
        return templateResolver
    }

}