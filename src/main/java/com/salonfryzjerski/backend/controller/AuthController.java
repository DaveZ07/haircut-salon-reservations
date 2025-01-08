package com.salonfryzjerski.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salonfryzjerski.backend.dto.LoginRequest;
import com.salonfryzjerski.backend.dto.LoginResponse;
import com.salonfryzjerski.backend.dto.MessageResponse;
import com.salonfryzjerski.backend.dto.RegisterRequest;
import com.salonfryzjerski.backend.model.User;
import com.salonfryzjerski.backend.security.JwtUtil;
import com.salonfryzjerski.backend.service.CustomUserDetails;
import com.salonfryzjerski.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autoryzacja", description = "API do zarządzania autoryzacją użytkowników")
@RequiredArgsConstructor
public class AuthController {
        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;

        @PostMapping("/login")
        @Operation(summary = "Logowanie użytkownika")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pomyślne zalogowanie"),
                        @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania")
        })
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(request.getUsername(),
                                                        request.getPassword()));

                        CustomUserDetails cud = (CustomUserDetails) authentication.getPrincipal();
                        Long userId = cud.getId();
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        String token = jwtUtil.generateToken(userDetails);
                        return ResponseEntity.ok(LoginResponse.builder()
                                        .token(token)
                                        .username(userDetails.getUsername())
                                        .role(userDetails.getAuthorities().stream().findFirst().get().getAuthority())
                                        .userId(userId)
                                        .message("Zalogowano pomyślnie")
                                        .build());
                } catch (AuthenticationException e) {
                        logger.error("Błąd podczas logowania: ", e);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse.builder()
                                        .token(null)
                                        .message("Nieprawidłowe dane logowania")
                                        .build());
                } catch (Exception e) {
                        logger.error("Nieoczekiwany błąd: ", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(LoginResponse.builder()
                                        .token(null)
                                        .message("Wystąpił nieoczekiwany błąd")
                                        .build());
                }
        }

        @PostMapping("/logout")
        @Operation(summary = "Wylogowanie użytkownika")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful logout"),
                        @ApiResponse(responseCode = "400", description = "Invalid token"),
                        @ApiResponse(responseCode = "401", description = "Missing authorization token")
        })
        public ResponseEntity<?> logout(HttpServletRequest request) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .body(LoginResponse.builder()
                                                        .message("Brak prawidłowego tokena autoryzacji")
                                                        .build());
                }
                try {
                        String jwt = authHeader.substring(7);
                        if (!jwtUtil.validateToken(jwt)) {
                                return ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(LoginResponse.builder()
                                                                .message("Nieprawidłowy token")
                                                                .build());
                        }
                        return ResponseEntity.ok(LoginResponse.builder()
                                        .message("Wylogowano pomyślnie")
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(LoginResponse.builder()
                                                        .message("Błąd podczas wylogowywania: " + e.getMessage())
                                                        .build());
                }
        }

        @Autowired
        private UserService userService;

        @PostMapping("/register")
        @Operation(summary = "Rejestracja użytkownika")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pomyślna rejestracja"),
                        @ApiResponse(responseCode = "400", description = "Użytkownik już istnieje")
        })
        public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
                if (userService.existsByUsername(request.getUsername())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Użytkownik o podanej nazwie już istnieje"));
                }

                if (userService.existsByEmail(request.getEmail())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Email jest już zajęty"));
                }

                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .email(request.getEmail())
                                .phoneNumber(request.getPhoneNumber())
                                .role(User.Role.ROLE_USER)
                                .build();

                userService.createUser(user);

                return ResponseEntity.ok(new MessageResponse("Użytkownik został zarejestrowany pomyślnie"));
        }

}