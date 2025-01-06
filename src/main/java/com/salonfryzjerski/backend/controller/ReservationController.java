package com.salonfryzjerski.backend.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salonfryzjerski.backend.model.Reservation;
import com.salonfryzjerski.backend.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/calendar")
    public Map<String, List<Map<String, String>>> getReservationsByDay() {
        List<Reservation> reservations = reservationService.getAllReservations();

        Map<LocalDate, List<Reservation>> groupedReservations = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getDate));

        Map<String, List<Map<String, String>>> calendarView = new TreeMap<>();
        groupedReservations.forEach((date, dailyReservations) -> {
            List<Map<String, String>> dailyView = dailyReservations.stream()
                    .map(reservation -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("startTime", reservation.getStartTime().toString());
                        map.put("endTime", reservation.getEndTime().toString());
                        map.put("serviceName", reservation.getService().getName());
                        map.put("customerName", reservation.getCustomerName());
                        return map;
                    })
                    .collect(Collectors.toList());

            calendarView.put(date.toString(), dailyView);
        });

        return calendarView;
    }

    @PostMapping
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.addReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation updatedReservation) {
        Reservation reservation = reservationService.updateReservation(id, updatedReservation);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }
}
