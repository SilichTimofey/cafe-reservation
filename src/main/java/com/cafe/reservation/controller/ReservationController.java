package com.cafe.reservation.controller;

import com.cafe.reservation.dto.reservation.ReservationRequest;
import com.cafe.reservation.dto.reservation.ReservationResponse;
import com.cafe.reservation.security.CustomUserDetails;
import com.cafe.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/me")
    public List<ReservationResponse> myReservations(@AuthenticationPrincipal CustomUserDetails principal) {
        return reservationService.findMyReservations(principal.getId());
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        return reservationService.findById(id, principal.getId(), isAdmin(principal));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        ReservationResponse created = reservationService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        return reservationService.cancel(id, principal.getId(), isAdmin(principal));
    }

    private boolean isAdmin(CustomUserDetails principal) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
