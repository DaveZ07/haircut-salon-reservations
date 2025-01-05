package com.salonfryzjerski.backend.repository;

import com.salonfryzjerski.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
