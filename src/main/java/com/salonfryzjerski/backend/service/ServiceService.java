package com.salonfryzjerski.backend.service;

import com.salonfryzjerski.backend.model.SalonService;
import com.salonfryzjerski.backend.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<SalonService> getAllServices() {
        return serviceRepository.findAll();
    }

    public SalonService createService(SalonService service) {
        return serviceRepository.save(service);
    }

    public SalonService updateService(Long id, SalonService serviceDetails) {
        return serviceRepository.findById(id).map(service -> {
            service.setName(serviceDetails.getName());
            service.setDuration(serviceDetails.getDuration());
            service.setPrice(serviceDetails.getPrice());
            return serviceRepository.save(service);
        }).orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public void deleteService(Long id) {
        serviceRepository.findById(id).ifPresent(service -> serviceRepository.delete(service));
    }
}
