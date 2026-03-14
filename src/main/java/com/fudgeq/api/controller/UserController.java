package com.fudgeq.api.controller;

import com.fudgeq.api.dto.LoginResponseDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.dto.UserDtoReturn;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.JWTTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final JWTTokenGenerator jwtTokenGenerator;
    private final ModelMapper mapper;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StandardResponse<UserDtoReturn>> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);
        UserDtoReturn profile = mapper.map(user, UserDtoReturn.class);

        return ResponseEntity.ok(
                StandardResponse.success("User profile retrieved successfully", profile)
        );
    }

    @PatchMapping("/upgrade-to-customer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StandardResponse<LoginResponseDto>> upgradeMyRole(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);

        if (user.getRole() != Role.VISITOR) {
            throw new RuntimeException("Only users with VISITOR role can upgrade to CUSTOMER.");
        }

        UserDtoReturn updatedUserDto = userService.changeUserRole(user.getUserId(), Role.CUSTOMER);
        User updatedUser = userService.getUserEntityById(user.getUserId());
        String newToken = jwtTokenGenerator.generateToken(updatedUser);

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .userId(updatedUserDto.getUserId())
                .email(updatedUserDto.getEmail())
                .firstName(updatedUserDto.getFirstName())
                .role(updatedUserDto.getRole())
                .status(updatedUserDto.getStatus())
                .token(newToken)
                .build();

        return ResponseEntity.ok(
                StandardResponse.success("Successfully upgraded to CUSTOMER. Session updated.", loginResponse)
        );
    }
}
