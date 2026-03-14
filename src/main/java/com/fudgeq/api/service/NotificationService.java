package com.fudgeq.api.service;

import com.fudgeq.api.dto.NotificationResponseDto;
import com.fudgeq.api.entity.User;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String title, String message, String orderId);
    List<NotificationResponseDto> getMyNotifications();
    void markAsRead(String notificationId);
    long getUnreadCount();
}
