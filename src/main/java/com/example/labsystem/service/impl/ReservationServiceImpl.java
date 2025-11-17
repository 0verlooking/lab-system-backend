package com.example.labsystem.service.impl;

import com.example.labsystem.domain.lab.Lab;
import com.example.labsystem.domain.reservation.Reservation;
import com.example.labsystem.domain.user.User;
import com.example.labsystem.dto.request.ReservationCreateRequest;
import com.example.labsystem.dto.request.ReservationStatusUpdateRequest;
import com.example.labsystem.dto.response.ReservationResponse;
import com.example.labsystem.exception.NotFoundException;
import com.example.labsystem.mapper.ReservationMapper;
import com.example.labsystem.repository.LabRepository;
import com.example.labsystem.repository.ReservationRepository;
import com.example.labsystem.repository.UserRepository;
import com.example.labsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    @Override
    public ReservationResponse create(ReservationCreateRequest request) {
        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new NotFoundException("Lab not found"));

        User user = getCurrentUser();

        Reservation reservation = ReservationMapper.toEntity(request, lab, user);
        reservationRepository.save(reservation);

        return ReservationMapper.toResponse(reservation);
    }

    @Override
    public ReservationResponse updateStatus(Long id, ReservationStatusUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        reservation.setStatus(request.getStatus());
        reservationRepository.save(reservation);

        return ReservationMapper.toResponse(reservation);
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
