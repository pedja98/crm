package com.etf.crm.config;

import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.filters.HeaderValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, new SecureRandom());

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HeaderValidationFilter headerValidationFilter, SetCurrentUserFilter setCurrentUserFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                ).addFilterBefore(headerValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(setCurrentUserFilter, HeaderValidationFilter.class);
        return http.build();
    }

    public static String encode(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
