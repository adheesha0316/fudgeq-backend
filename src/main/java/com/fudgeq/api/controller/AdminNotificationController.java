package com.fudgeq.api.controller;

import com.fudgeq.api.dto.NotificationResponseDto;
import com.fudgeq.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminNotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications sent to a specific user to track their engagement
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    /**
     * Send a manual custom notification to a user for specific updates
     */
    @PostMapping("/send-manual")
    public ResponseEntity<Void> sendManualNotification(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String message) {
        notificationService.sendManualNotification(userId, title, message);
        return ResponseEntity.ok().build();
    }
}
