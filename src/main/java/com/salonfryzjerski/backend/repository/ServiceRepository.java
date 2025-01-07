package com.salonfryzjerski.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salonfryzjerski.backend.model.SalonService;

public interface ServiceRepository extends JpaRepository<SalonService, Long> {
}
