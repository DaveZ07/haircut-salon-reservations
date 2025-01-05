package com.salonfryzjerski.backend.service;

import com.salonfryzjerski.backend.model.Reservation;
import com.salonfryzjerski.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setDateTime(reservationDetails.getDateTime());
            reservation.setStatus(reservationDetails.getStatus());
            return reservationRepository.save(reservation);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public void deleteReservation(Long id) {
        reservationRepository.findById(id).ifPresent(reservation -> reservationRepository.delete(reservation));
    }
}
