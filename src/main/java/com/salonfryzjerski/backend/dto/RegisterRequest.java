package com.salonfryzjerski.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, max = 40, message = "Hasło musi mieć od 6 do 40 znaków")
    private String password;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    private String email;

    @NotBlank(message = "Numer telefonu jest wymagany")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Nieprawidłowy format numeru telefonu")
    private String phoneNumber;
}