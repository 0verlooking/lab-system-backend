package com.example.labsystem.service;

import com.example.labsystem.dto.request.ReservationCreateRequest;
import com.example.labsystem.dto.request.ReservationStatusUpdateRequest;
import com.example.labsystem.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse create(ReservationCreateRequest request);

    ReservationResponse updateStatus(Long id, ReservationStatusUpdateRequest request);

    List<ReservationResponse> myReservations();

    List<ReservationResponse> allReservations();

    void delete(Long id);
}
