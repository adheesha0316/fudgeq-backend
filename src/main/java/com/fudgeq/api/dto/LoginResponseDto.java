package com.fudgeq.api.dto;

import com.fudgeq.api.enums.Role;
import com.fudgeq.api.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String userId;
    private String email;
    private UserDtoReturn user;
    private String token;
    private Role role;
    private String firstName;
    private UserStatus status;
}
