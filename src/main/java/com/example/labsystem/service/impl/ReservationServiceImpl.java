package com.example.labsystem.service.impl;

import com.example.labsystem.domain.lab.Lab;
import com.example.labsystem.domain.reservation.Reservation;
import com.example.labsystem.domain.reservation.ReservationStatus;
import com.example.labsystem.domain.user.User;
import com.example.labsystem.dto.request.ReservationCreateRequest;
import com.example.labsystem.dto.request.ReservationStatusUpdateRequest;
import com.example.labsystem.dto.response.ReservationResponse;
import com.example.labsystem.exception.NotFoundException;
import com.example.labsystem.mapper.ReservationMapper;
import com.example.labsystem.pattern.observer.ReservationEvent;
import com.example.labsystem.pattern.observer.ReservationEventPublisher;
import com.example.labsystem.pattern.strategy.ReservationValidator;
import com.example.labsystem.repository.LabRepository;
import com.example.labsystem.repository.ReservationRepository;
import com.example.labsystem.repository.UserRepository;
import com.example.labsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реалізація сервісу резервацій.
 * Застосовує SOLID принципи та Design Patterns:
 * - Single Responsibility: відповідає лише за бізнес-логіку резервацій
 * - Strategy Pattern: використовує ReservationValidator для валідації
 * - Observer Pattern: публікує події через ReservationEventPublisher
 * - Dependency Inversion: залежить від абстракцій
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;
    private final ReservationValidator reservationValidator;
    private final ReservationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ReservationResponse create(ReservationCreateRequest request) {
        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new NotFoundException("Lab not found"));

        User user = getCurrentUser();

        Reservation reservation = ReservationMapper.toEntity(request, lab, user);

        // Використання Strategy Pattern для валідації
        reservationValidator.validateAll(reservation);

        reservation.setStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);

        // Використання Observer Pattern для сповіщення
        eventPublisher.publishEvent(new ReservationEvent(
                reservation,
                ReservationEvent.ReservationEventType.CREATED,
                user.getUsername()
        ));

        return ReservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateStatus(Long id, ReservationStatusUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        ReservationStatus oldStatus = reservation.getStatus();
        reservation.setStatus(request.getStatus());
        reservationRepository.save(reservation);

        // Публікація події в залежності від зміни статусу
        ReservationEvent.ReservationEventType eventType = determineEventType(oldStatus, request.getStatus());
        if (eventType != null) {
            eventPublisher.publishEvent(new ReservationEvent(
                    reservation,
                    eventType,
                    reservation.getUser().getUsername()
            ));
        }

        return ReservationMapper.toResponse(reservation);
    }

    private ReservationEvent.ReservationEventType determineEventType(ReservationStatus oldStatus, ReservationStatus newStatus) {
        if (newStatus == ReservationStatus.APPROVED && oldStatus != ReservationStatus.APPROVED) {
            return ReservationEvent.ReservationEventType.APPROVED;
        } else if (newStatus == ReservationStatus.CANCELLED) {
            return ReservationEvent.ReservationEventType.CANCELLED;
        }
        return ReservationEvent.ReservationEventType.UPDATED;
    }

    @Override
    public List<ReservationResponse> myReservations() {
        User user = getCurrentUser();
        return reservationRepository.findByUser(user).stream()
                .map(res -> ReservationMapper.toResponse(res))
                .toList();
    }

    @Override
    public List<ReservationResponse> allReservations() {
        return reservationRepository.findAll().stream()
                .map(res -> ReservationMapper.toResponse(res))
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Current user not found"));
    }
}
