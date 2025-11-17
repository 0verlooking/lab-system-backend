package com.example.labsystem.dto.response;

import com.example.labsystem.domain.lab.EquipmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EquipmentResponse {

    private Long id;
    private String name;
    private String inventoryNumber;
    private EquipmentStatus status;

    private Long labId;
    private String labName;
}
