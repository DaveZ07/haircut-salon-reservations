package com.salonfryzjerski.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salonfryzjerski.backend.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
