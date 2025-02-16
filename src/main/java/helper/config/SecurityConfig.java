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

            .antMatchers(HttpMethod.GET, "/img/*").permitAll()

            .antMatchers(HttpMethod.GET, "/", "/css/common.css", "/img/globe.png").permitAll()   // Start page
            .antMatchers(HttpMethod.GET, "/", "/css/start.css", "/js/start.js").permitAll()   // Start page
            .antMatchers(HttpMethod.GET, "/register", "/css/register.css", "/js/register.js", "/api/language").permitAll() // Register page
            .antMatchers(HttpMethod.POST, "/api/student").permitAll()  // Register a student
            .antMatchers(HttpMethod.GET, "/auth/login", "/css/login.css", "/js/login.js").permitAll()   // Login page

            .antMatchers(HttpMethod.GET, "/main", "/css/main.css").authenticated()   // Start page

            .antMatchers(HttpMethod.GET, "/setroles").hasAuthority("Admin") // Set roles page
            .antMatchers(HttpMethod.GET, "/css/roles.css", "/js/roles.js").hasAuthority("Admin") // Edit student (set roles)
            .antMatchers(HttpMethod.GET, "/api/student/data").hasAuthority("Admin")  // Edit student (set roles)
            .antMatchers(HttpMethod.PUT, "/api/student/data").hasAuthority("Admin")  // Edit student (set roles)

            .antMatchers(HttpMethod.GET, "/settings").hasAuthority("Admin") // Set settings page
            .antMatchers(HttpMethod.POST, "/api/settings/background/sounding").hasAuthority("Admin") // Set settings page
            .antMatchers(HttpMethod.GET, "/css/settings.css", "/js/settings.js").hasAuthority("Admin") // Edit application settings

            .antMatchers(HttpMethod.GET, "/word", "/css/word.css", "/js/word*", "/js/sound.js", "/img/home*", "/img/swap*").hasAuthority("Editor") // Edit word page
            .antMatchers(HttpMethod.GET, "/img/correct.png", "/snd/*").hasAuthority("Editor") // Edit word page
            .antMatchers(HttpMethod.POST, "/api/word", "/api/word/translate").hasAuthority("Editor") // Edit word page
            .antMatchers(HttpMethod.DELETE, "/api/word/translate").hasAuthority("Editor") // Edit word page
            .antMatchers(HttpMethod.POST, "/api/word/voice/random").hasAuthority("Editor") // Edit word page
            .antMatchers(HttpMethod.GET, "/api/voice").hasAuthority("Editor") // Edit word page

            .antMatchers(HttpMethod.GET, "/api/voice/test").hasAuthority("Editor") // Edit word page

            .antMatchers(HttpMethod.GET, "/sounding", "/css/sounding.css", "/js/sounding.js").hasAuthority("Editor")   // Sounding page
            .antMatchers(HttpMethod.GET, "/api/voice/random").hasAuthority("Editor") // Sounding page
            .antMatchers(HttpMethod.GET, "/api/voice/random/word").hasAuthority("Editor") // Sounding page
            .antMatchers(HttpMethod.POST, "/api/voice/voices").hasAuthority("Editor") // Sounding page
            .antMatchers(HttpMethod.GET, "/api/settings/background/sounding").hasAuthority("Editor") // Sounding page

            .antMatchers(HttpMethod.GET, "/training", "/css/training.css", "/css/language.css", "/js/training.js").hasAuthority("Student") // Training page
            .antMatchers(HttpMethod.GET, "/img/go.png", "/img/stop.png", "/img/send*", "/img/wrong.png").hasAuthority("Student") // Training page
            .antMatchers(HttpMethod.GET, "/snd/success.mp3", "/snd/error.mp3", "/snd/alert.mp3").hasAuthority("Student") // Training page
            .antMatchers(HttpMethod.POST, "/api/training").hasAuthority("Student") // Training page
            .antMatchers(HttpMethod.POST, "/api/training/result").hasAuthority("Student") // Training page

            .antMatchers(HttpMethod.GET, "/collection", "/css/collection.css", "/js/collection.js", "/js/language.js").hasAuthority("Student") // Collection edit page
            .antMatchers(HttpMethod.GET, "/img/new.png", "/css/language.css", "/img/arrows.png", "/img/delete.png", "/img/home*").hasAuthority("Student") // Collection edit page
            .antMatchers(HttpMethod.GET, "/api/collection/names").hasAuthority("Student")   // Getting collection names
            .antMatchers(HttpMethod.POST, "/api/collection/translation").hasAuthority("Student")   // Searching translation
            .antMatchers(HttpMethod.POST, "/api/collection").hasAuthority("Student")    // Send a collection
            .antMatchers(HttpMethod.GET, "/api/collection").hasAuthority("Student")    // Get collection content

            .antMatchers(HttpMethod.GET, "/export", "/css/export.css", "/js/export.js", "/img/plus*").hasAuthority("Editor")  // export page
            .antMatchers(HttpMethod.GET, "/api/translation/languages").hasAuthority("Editor")
            .antMatchers(HttpMethod.POST, "/api/translation/export").hasAuthority("Editor")

//                .anyRequest().authenticated()
            .anyRequest().denyAll()
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
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
