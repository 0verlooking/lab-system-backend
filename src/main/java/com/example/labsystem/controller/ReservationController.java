package com.example.labsystem.controller;

import com.example.labsystem.dto.request.ReservationCreateRequest;
import com.example.labsystem.dto.request.ReservationStatusUpdateRequest;
import com.example.labsystem.dto.response.ReservationResponse;
import com.example.labsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationCreateRequest request) {
        return ResponseEntity.ok(reservationService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(reservationService.updateStatus(id, request));
    }

    /**
     * Резервації поточного користувача (витягується з JWT).
     */
    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myReservations() {
        return ResponseEntity.ok(reservationService.myReservations());
    }

    /**
     * Усі резервації (для ролі ADMIN / LAB_MANAGER – перевіряється в SecurityConfig).
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll() {
        return ResponseEntity.ok(reservationService.allReservations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
