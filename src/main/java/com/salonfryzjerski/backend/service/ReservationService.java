package com.salonfryzjerski.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.salonfryzjerski.backend.model.Reservation;
import com.salonfryzjerski.backend.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    public List<Reservation> getReservationsByCustomer(String customerEmail) {
        return reservationRepository.findByCustomerEmail(customerEmail);
    }

    public Reservation addReservation(Reservation reservation) {
        List<Reservation> existingReservations = reservationRepository.findAll();

        boolean isTimeSlotTaken = existingReservations.stream()
                .anyMatch(existing -> existing.getDate().equals(reservation.getDate()) &&
                        ((reservation.getStartTime().isBefore(existing.getEndTime())
                                && reservation.getStartTime().isAfter(existing.getStartTime())) ||
                                (reservation.getEndTime().isAfter(existing.getStartTime())
                                        && reservation.getEndTime().isBefore(existing.getEndTime()))
                                ||
                                (reservation.getStartTime().equals(existing.getStartTime()))));

        if (isTimeSlotTaken) {
            throw new RuntimeException("Wybrana godzina jest już zajęta");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        return reservationRepository.findById(id).map(existingReservation -> {
            existingReservation.setDate(updatedReservation.getDate());
            existingReservation.setStartTime(updatedReservation.getStartTime());
            existingReservation.setEndTime(updatedReservation.getEndTime());
            existingReservation.setCustomerName(updatedReservation.getCustomerName());
            existingReservation.setService(updatedReservation.getService());
            return reservationRepository.save(existingReservation);
        }).orElseThrow(() -> new RuntimeException("Rezerwacja o ID " + id + " nie istnieje"));
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Rezerwacja o ID " + id + " nie istnieje");
        }
        reservationRepository.deleteById(id);
    }

}
