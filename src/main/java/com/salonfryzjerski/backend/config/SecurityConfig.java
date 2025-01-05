package com.salonfryzjerski.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.salonfryzjerski.backend.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Wyłączenie CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/services/**").permitAll() // Endpointy publiczne
                        .anyRequest().authenticated()) // Reszta wymaga logowania
                .formLogin(login -> login.defaultSuccessUrl("/").permitAll()) // Formularz logowania
                .logout(logout -> logout.permitAll()); // Wylogowanie dostępne dla każdego

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
