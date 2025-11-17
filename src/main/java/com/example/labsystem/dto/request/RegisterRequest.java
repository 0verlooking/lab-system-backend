package com.example.labsystem.dto.request;

import com.example.labsystem.domain.user.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private UserRole role; // можна не передавати, тоді буде STUDENT
}
