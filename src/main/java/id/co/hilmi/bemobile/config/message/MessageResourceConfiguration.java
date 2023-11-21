package id.co.hilmi.bemobile.config.message;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
@Configuration
public class MessageResourceConfiguration {
    public MessageResourceConfiguration() {
    }

    @Bean()
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(new String[]{"classpath:/messages/general"});
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
