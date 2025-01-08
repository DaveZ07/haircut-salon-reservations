package com.salonfryzjerski.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salonfryzjerski.backend.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerEmail(String customerEmail);

}
