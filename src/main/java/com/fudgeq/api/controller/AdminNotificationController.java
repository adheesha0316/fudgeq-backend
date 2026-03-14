package com.fudgeq.api.controller;

import com.fudgeq.api.dto.NotificationResponseDto;
import com.fudgeq.api.dto.StandardResponse;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<StandardResponse<List<NotificationResponseDto>>> getUserNotifications(@PathVariable String userId) {
        List<NotificationResponseDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(
                StandardResponse.success("Engagement notifications for user " + userId + " retrieved", notifications)
        );
    }

    @PostMapping("/send-manual")
    public ResponseEntity<StandardResponse<Void>> sendManualNotification(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String message) {
        notificationService.sendManualNotification(userId, title, message);
        return ResponseEntity.ok(
                StandardResponse.success("Manual notification sent successfully to user: " + userId, null)
        );
    }
}
