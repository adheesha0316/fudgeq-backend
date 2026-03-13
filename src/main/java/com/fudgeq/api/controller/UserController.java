package com.fudgeq.api.controller;

import com.fudgeq.api.dto.UserDtoReturn;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.JWTTokenGenerator;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDtoReturn> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDtoReturn user = userService.getUserById(userService.getUserEntityByEmail(email).getUserId())
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDtoReturn> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDtoReturn>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PatchMapping("/upgrade-to-customer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> upgradeMyRole(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserEntityByEmail(email);

        // Validate if the user is currently a VISITOR
        if (user.getRole() != Role.VISITOR) {
            throw new RuntimeException("Only users with VISITOR role can upgrade to CUSTOMER.");
        }

        // Update user role to CUSTOMER in the database
        UserDtoReturn updatedUserDto = userService.changeUserRole(user.getUserId(), Role.CUSTOMER);

        // Fetch the updated entity to generate a new JWT with updated claims
        User updatedUser = userService.getUserEntityById(user.getUserId());
        String newToken = jwtTokenGenerator.generateToken(updatedUser);

        // Prepare combined response with user data and new access token
        Map<String, Object> response = new HashMap<>();
        response.put("user", updatedUserDto);
        response.put("accessToken", newToken);
        response.put("message", "Successfully upgraded to CUSTOMER. Please update your session token.");

        return ResponseEntity.ok(response);
    }
}
