package mytool.backend

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@CompileStatic
@Configuration
class WebConfig extends WebMvcConfigurationSupport {
    @Override
    void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/", "classpath:/js/");
        registry.addResourceHandler("/html/**")
                .addResourceLocations("/", "classpath:/html/");
        registry.addResourceHandler("/json/**")
                .addResourceLocations("/", "classpath:/json/");
    }
}
