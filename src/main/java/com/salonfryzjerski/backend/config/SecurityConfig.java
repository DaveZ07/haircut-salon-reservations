package com.salonfryzjerski.backend.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.salonfryzjerski.backend.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        try {
            Path logPath = Paths.get(System.getProperty("user.dir"), "logs");
            Files.createDirectories(logPath);
            logger.info("Próba utworzenia katalogu logs w: {}", logPath.toAbsolutePath());
            
            if (Files.exists(logPath)) {
                logger.info("Katalog logs został utworzony pomyślnie");
                Path testFile = logPath.resolve("test.log");
                Files.writeString(testFile, "Test zapisu");
                logger.info("Utworzono plik testowy: {}", testFile.toAbsolutePath());
                
                logger.debug("Test debug message");
                logger.info("Test info message");
                logger.warn("Test warning message");
                logger.error("Test error message");
            }
        } catch (Exception e) {
            logger.error("Błąd podczas tworzenia katalogu logs: ", e);
        }
        this.customUserDetailsService = customUserDetailsService;
        logger.info("SecurityConfig initialized");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("Configuring SecurityFilterChain");
        http
            .csrf(csrf -> {
                csrf.disable();
                logger.debug("CSRF disabled");
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/services/**").permitAll()
                .requestMatchers("/api/reservations/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    response.sendError(HttpStatus.FORBIDDEN.value())))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults());

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
