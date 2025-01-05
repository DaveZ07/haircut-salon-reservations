package com.salonfryzjerski.backend.controller;

import com.salonfryzjerski.backend.model.Service;
import com.salonfryzjerski.backend.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = serviceRepository.save(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
        return serviceRepository.findById(id).map(service -> {
            service.setName(serviceDetails.getName());
            service.setDuration(serviceDetails.getDuration());
            service.setPrice(serviceDetails.getPrice());
            Service updatedService = serviceRepository.save(service);
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
