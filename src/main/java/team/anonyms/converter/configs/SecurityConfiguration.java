package team.anonyms.converter.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.anonyms.converter.security.RequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final RequestFilter requestFilter;

    public SecurityConfiguration(RequestFilter requestFilter) {
        this.requestFilter = requestFilter;
    }

    /**
     * Configures requirement of authorization for every HTTP request. <br>
     * Uses {@link RequestFilter} to authenticate requests.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        /*
        Note, that .anyRequest().authenticated() should be applied only to the last .requestMatchers() method.
        Otherwise, there will be a bug on start up.
        */
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/conversion/**", "/auth/password/**", "/direct/**")
                        .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth", "/users")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}