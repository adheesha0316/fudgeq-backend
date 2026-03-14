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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('CUSTOMER')")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<StandardResponse<List<NotificationResponseDto>>> getMyNotifications() {
        List<NotificationResponseDto> notifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(
                StandardResponse.success("Notifications retrieved successfully", notifications)
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<StandardResponse<Long>> getUnreadCount() {
        Long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(
                StandardResponse.success("Unread notification count retrieved", count)
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<StandardResponse<Void>> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                StandardResponse.success("Notification marked as read", null)
        );
    }
}
