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
public class UserDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private UserStatus status;
}
