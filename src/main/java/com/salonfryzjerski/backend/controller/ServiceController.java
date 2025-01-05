package com.salonfryzjerski.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<SalonService> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<SalonService> createService(@RequestBody SalonService service) {
        SalonService savedService = serviceRepository.save(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceRepository.findById(id).map(service -> {
            serviceRepository.delete(service);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
