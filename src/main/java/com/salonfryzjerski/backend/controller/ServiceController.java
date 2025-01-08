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

import com.salonfryzjerski.backend.model.SalonService;
import com.salonfryzjerski.backend.repository.ServiceRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Usługi", description = "API do zarządzania usługami salonu")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    @Operation(summary = "Pobranie wszystkich usług")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślne pobranie usług")
    })
    public List<SalonService> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Dodanie nowej usługi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pomyślne dodanie usługi"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane usługi")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonService> createService(@RequestBody SalonService service) {
        SalonService savedService = serviceRepository.save(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizacja usługi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślne zaktualizowanie usługi"),
            @ApiResponse(responseCode = "404", description = "Usługa nie znaleziona")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonService> updateService(@PathVariable Long id, @RequestBody SalonService serviceDetails) {
        return serviceRepository.findById(id).map(service -> {
            service.setName(serviceDetails.getName());
            service.setDuration(serviceDetails.getDuration());
            service.setPrice(serviceDetails.getPrice());
            SalonService updatedService = serviceRepository.save(service);
            return new ResponseEntity<>(updatedService, HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usunięcie usługi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pomyślne usunięcie usługi"),
            @ApiResponse(responseCode = "404", description = "Usługa nie znaleziona")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceRepository.findById(id).map(service -> {
            serviceRepository.delete(service);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
