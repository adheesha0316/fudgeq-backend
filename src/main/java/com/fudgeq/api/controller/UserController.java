package com.fudgeq.api.controller;

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
    public ResponseEntity<UserDtoReturn> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);
        return ResponseEntity.ok(mapper.map(user, UserDtoReturn.class));
    }

    @PatchMapping("/upgrade-to-customer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> upgradeMyRole(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);

        if (user.getRole() != Role.VISITOR) {
            throw new RuntimeException("Only users with VISITOR role can upgrade to CUSTOMER.");
        }

        UserDtoReturn updatedUserDto = userService.changeUserRole(user.getUserId(), Role.CUSTOMER);
        User updatedUser = userService.getUserEntityById(user.getUserId());
        String newToken = jwtTokenGenerator.generateToken(updatedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", updatedUserDto);
        response.put("accessToken", newToken);
        response.put("message", "Successfully upgraded to CUSTOMER. Your session has been updated.");

        return ResponseEntity.ok(response);
    }
}
