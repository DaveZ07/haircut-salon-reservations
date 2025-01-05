package com.salonfryzjerski.backend.repository;

import com.salonfryzjerski.backend.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<SalonService, Long> {
}
