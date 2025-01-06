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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Rezerwacje", description = "Zarządzanie rezerwacjami w systemie")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Pobierz kalendarz rezerwacji", 
            description = "Zwraca wszystkie rezerwacje pogrupowane według dat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano kalendarz rezerwacji"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
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

    @Operation(summary = "Dodaj nową rezerwację", 
            description = "Tworzy nową rezerwację w systemie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rezerwacja została utworzona"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane rezerwacji"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
    @PostMapping
    public ResponseEntity<Reservation> addReservation(
            @Parameter(description = "Dane nowej rezerwacji") 
            @RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.addReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @Operation(summary = "Aktualizuj rezerwację", 
            description = "Aktualizuje istniejącą rezerwację na podstawie ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rezerwacja została zaktualizowana"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane rezerwacji"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @Parameter(description = "ID rezerwacji do aktualizacji") 
            @PathVariable Long id,
            @Parameter(description = "Zaktualizowane dane rezerwacji") 
            @RequestBody Reservation updatedReservation) {
        Reservation reservation = reservationService.updateReservation(id, updatedReservation);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Usuń rezerwację", 
            description = "Usuwa rezerwację o podanym ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rezerwacja została usunięta"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "ID rezerwacji do usunięcia") 
            @PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }
}
