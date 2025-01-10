package com.salonfryzjerski.backend.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.salonfryzjerski.backend.model.User;
import com.salonfryzjerski.backend.service.ReservationService;
import com.salonfryzjerski.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Rezerwacje", description = "API do zarządzania rezerwacjami")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;

    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
        }

        @Operation(summary = "Pobierz kalendarz rezerwacji", description = "Zwraca wszystkie rezerwacje pogrupowane według dat")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano kalendarz rezerwacji"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
        })
        @GetMapping("/calendar")
        public Map<String, List<Map<String, Object>>> getReservationsByDay() {
        List<Reservation> reservations = reservationService.getAllReservations();

        Map<LocalDate, List<Reservation>> groupedReservations = reservations.stream()
            .collect(Collectors.groupingBy(Reservation::getDate));

        Map<String, List<Map<String, Object>>> calendarView = new TreeMap<>();
        LocalDate today = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        while (endDate.getMonthValue() < 12) {
            endDate = endDate.plusMonths(1).withDayOfMonth(endDate.plusMonths(1).lengthOfMonth());
        }

        while (!today.isAfter(endDate)) {
            List<Map<String, Object>> dailyView = new ArrayList<>();

            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(16, 0);

            while (!startTime.isAfter(endTime)) {
            Map<String, Object> timeSlot = new HashMap<>();
            timeSlot.put("startTime", startTime.toString());
            timeSlot.put("endTime", startTime.plusMinutes(30).toString());
            timeSlot.put("reserved", false);

            dailyView.add(timeSlot);
            startTime = startTime.plusMinutes(30);
            }

            List<Reservation> dailyReservations = groupedReservations.getOrDefault(today, new ArrayList<>());
            dailyReservations.forEach(reservation -> {
            dailyView.forEach(timeSlot -> {
                LocalTime slotStartTime = LocalTime.parse((String) timeSlot.get("startTime"));
                LocalTime slotEndTime = LocalTime.parse((String) timeSlot.get("endTime"));

                if (!reservation.getStartTime().isAfter(slotEndTime)
                            && !reservation.getEndTime().isBefore(slotStartTime)) {
                timeSlot.put("reserved", true);
                timeSlot.put("serviceName", reservation.getService().getName());
                timeSlot.put("reservationId", reservation.getId());
                }
            });
            });

            calendarView.put(today.toString(), dailyView);
            today = today.plusDays(1);
        }

        return calendarView;
        }

        @Operation(summary = "Pobierz wszystkie rezerwacje należące do użytkownika o podanym ID", description = "Zwraca listę wszystkich rezerwacji w systemie użytkownika o podanym emailu lub numerze telefonu")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano rezerwacje"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
        })
        @GetMapping("/customer/{customerID}")
        public List<Reservation> getReservationsByCustomer(
            @Parameter(description = "ID użytkownika") @PathVariable String customerID) {
        Optional<User> customerOptional = userService.getUserById(Long.valueOf(customerID));
        if (customerOptional.isPresent()) {
            User customer = customerOptional.get();
            return reservationService.getReservationsByCustomer(customer.getEmail());
        } else {
            throw new IllegalArgumentException("User not found with ID: " + customerID);
        }
    }

    @Operation(summary = "Dodaj nową rezerwację", description = "Dodaje nową rezerwację")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rezerwacja została utworzona"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane rezerwacji"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
    @PostMapping
    public ResponseEntity<Reservation> addReservation(
            @Parameter(description = "Dane nowej rezerwacji") @RequestBody Reservation reservation) {
        if (reservation.getDate().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        logger.debug("Dodawanie nowej rezerwacji: {}", reservation);
        Reservation savedReservation = reservationService.addReservation(reservation);
        logger.info("Rezerwacja została utworzona: {}", savedReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @Operation(summary = "Aktualizuj rezerwację", description = "Aktualizuje istniejącą rezerwację na podstawie ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezerwacja została zaktualizowana"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane rezerwacji"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @Parameter(description = "ID rezerwacji do aktualizacji") @PathVariable Long id,
            @Parameter(description = "Zaktualizowane dane rezerwacji") @RequestBody Reservation updatedReservation) {
        if (updatedReservation.getDate().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        logger.debug("Aktualizacja rezerwacji o ID: {}", id);
        Reservation reservation = reservationService.updateReservation(id, updatedReservation);
        logger.info("Rezerwacja została zaktualizowana: {}", reservation);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Usuń rezerwację", description = "Usuwa rezerwację o podanym ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezerwacja została usunięta"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono rezerwacji")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "ID rezerwacji do usunięcia") @PathVariable Long id) {
        logger.debug("Usuwanie rezerwacji o ID: {}", id);
        reservationService.deleteReservation(id);
        logger.info("Rezerwacja została usunięta o ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
