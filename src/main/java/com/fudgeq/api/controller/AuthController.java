package com.fudgeq.api.controller;

import com.fudgeq.api.dto.*;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.UserStatus;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.JWTTokenGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final JWTTokenGenerator jwtTokenGenerator;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<UserDtoReturn>> registerUser(@Valid @RequestBody UserDto userDto) {
        UserDtoReturn registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(
                StandardResponse.success("User registered successfully. Please wait for approval.", registeredUser)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        User user = userService.getUserEntityByEmail(loginRequestDto.getEmail());

        if (user.getStatus() != UserStatus.APPROVED) {
            throw new RuntimeException("Authentication failed: User account is " + user.getStatus());
        }

        if (!user.isActive()) {
            throw new RuntimeException("Authentication failed: User account is deactivated.");
        }

        LoginResponseDto loginResponse = userService.loginUser(loginRequestDto);
        String token = jwtTokenGenerator.generateToken(user);
        loginResponse.setToken(token);

        return ResponseEntity.ok(
                StandardResponse.success("Login successful", loginResponse)
        );
    }
}
