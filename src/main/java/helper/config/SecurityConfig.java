package helper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/css/common.css", "/img/globe.png").permitAll()   // Start page
                .antMatchers(HttpMethod.GET, "/", "/css/start.css", "/js/start.js").permitAll()   // Start page
                .antMatchers(HttpMethod.GET, "/register", "/css/register.css", "/js/register.js", "/api/language").permitAll() // Register page
                .antMatchers(HttpMethod.POST, "/api/student").permitAll()  // Register a student
                .antMatchers(HttpMethod.GET, "/auth/login", "/css/login.css", "/js/login.js").permitAll()   // Login page
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/auth/login").permitAll()
                .and().logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
        ;
        return http.build();
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return new StudentDetailsService();
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
