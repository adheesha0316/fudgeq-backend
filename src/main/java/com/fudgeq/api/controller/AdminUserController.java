package com.fudgeq.api.controller;

import com.fudgeq.api.dto.UserDtoReturn;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<Page<UserDtoReturn>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDtoReturn> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<UserDtoReturn> approveUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @PatchMapping("/disapprove/{id}")
    public ResponseEntity<UserDtoReturn> disapproveUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.disapproveUser(id));
    }

    @PatchMapping("/change-role/{id}")
    public ResponseEntity<UserDtoReturn> changeRole(@PathVariable String id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }
}
