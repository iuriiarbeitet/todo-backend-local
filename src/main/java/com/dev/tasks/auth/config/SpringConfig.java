package com.dev.tasks.auth.config;

import com.dev.tasks.auth.filter.AuthTokenFilter;
import com.dev.tasks.auth.filter.ExceptionHandlerFilter;
import com.dev.tasks.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
@EnableAsync
public class SpringConfig extends WebSecurityConfigurerAdapter {


    private UserDetailsServiceImpl userDetailsService;
    private AuthTokenFilter authTokenFilter;
    private ExceptionHandlerFilter exceptionHandlerFilter;

    @Autowired
    public void setExceptionHandlerFilter(ExceptionHandlerFilter exceptionHandlerFilter) {
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) { // внедряем наш компонент Spring @Service
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setAuthTokenFilter(AuthTokenFilter authTokenFilter) { // внедряем фильтр
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception { // настройки AuthenticationManager для правильной проверки логин-пароль
        authenticationManagerBuilder.
                userDetailsService(userDetailsService). // использовать наш сервис для загрузки User из БД
                passwordEncoder(passwordEncoder()); // указываем, что используется кодировщик пароля (для корректной проверки пароля)
    }

    @Bean
    public FilterRegistrationBean registration(AuthTokenFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter); // FilterRegistrationBean - регистратор фильтров для сервлет контейнера
        registration.setEnabled(false); // отключить исп-е фильтра для сервлет контейнера
        return registration;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable();

        http.formLogin().disable();
        http.httpBasic().disable();

        http.requiresChannel().anyRequest().requiresSecure();

        http.addFilterBefore(authTokenFilter, SessionManagementFilter.class);

        http.addFilterBefore(exceptionHandlerFilter, AuthTokenFilter.class);
    }

}
