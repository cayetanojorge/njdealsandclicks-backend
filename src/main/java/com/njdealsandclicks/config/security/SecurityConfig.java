package com.njdealsandclicks.config.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    private static final List<String> LOCAL_PROFILES = List.of("develop", "test");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                if (LOCAL_PROFILES.contains(activeProfile)) {
                    // Ambiente locale: tutto permesso
                    auth.requestMatchers("/**").permitAll();
                } else {
                    // Produzione: solo API pubbliche accessibili dal frontend
                    auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").denyAll()
                        .requestMatchers("/api/**").denyAll();
                }
            })
            .build();
    }
}
