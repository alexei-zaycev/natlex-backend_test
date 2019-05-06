package ru.net.avz.test.natlex_backend_test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import ru.net.avz.test.natlex_backend_test.Utils;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig
        extends WebSecurityConfigurerAdapter {

    public static final String ROLE__ADMIN      = "ADMIN";
    public static final String ROLE__USER       = "USER";

    @Autowired private BasicAuthenticationEntryPoint authEntryPoint;

    @Override
    protected void configure(
            HttpSecurity http)
            throws Exception {

        http.csrf()
                .disable();

        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        http.httpBasic()
                .authenticationEntryPoint(Utils.requireDI(BasicAuthenticationEntryPoint.class, authEntryPoint));

        http.logout()
                .permitAll()
                .deleteCookies("remove")
                .invalidateHttpSession(true);
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth)
            throws Exception {

        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode("admin")).roles(ROLE__ADMIN, ROLE__USER).and()
                .withUser("user").password(passwordEncoder().encode("user")).roles(ROLE__USER);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}