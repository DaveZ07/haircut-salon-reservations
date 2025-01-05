package com.salonfryzjerski.backend.controller;

import com.salonfryzjerski.backend.model.Reservation;
import com.salonfryzjerski.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setDateTime(reservationDetails.getDateTime());
            reservation.setStatus(reservationDetails.getStatus());
            Reservation updated = reservationRepository.save(reservation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id).map(reservation -> {
            reservationRepository.delete(reservation);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
