package ru.net.avz.test.natlex_backend_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@EnableAsync
@EnableJpaRepositories
//@EnableTransactionManagement
@EnableWebMvc
@SpringBootApplication
public class App {

    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }

    @Bean    // игнорируем завершающие '/' в конце урлов
    public RequestMappingHandlerMapping useTrailingSlash() {
        return new RequestMappingHandlerMapping() {{ setUseTrailingSlashMatch(true); }};
    }

}