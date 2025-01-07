package com.salonfryzjerski.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salonfryzjerski.backend.model.User;
import com.salonfryzjerski.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Użytkownicy", description = "API do zarządzania użytkownikami")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Pobranie wszystkich użytkowników")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślne pobranie użytkowników")
    })
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobranie użytkownika po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślne pobranie użytkownika"),
            @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
    })
    public ResponseEntity<User> getUserById(
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Dodanie nowego użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pomyślne dodanie użytkownika"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane użytkownika")
    })
    public ResponseEntity<User> createUser(
            @RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizacja użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślne zaktualizowanie użytkownika"),
            @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
    })
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usunięcie użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pomyślne usunięcie użytkownika"),
            @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
