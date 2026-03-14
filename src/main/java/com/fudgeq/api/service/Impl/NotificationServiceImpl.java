package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.NotificationResponseDto;
import com.fudgeq.api.entity.Notification;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.repo.NotificationRepo;
import com.fudgeq.api.repo.UserRepo;
import com.fudgeq.api.service.NotificationService;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserService userService;
    private final UserRepo userRepo;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void createNotification(User user, String title, String message, String orderId) {
        Notification notification = Notification.builder()
                .notificationId(idGenerator.generateNextId(AppConstants.PREFIX_NOTIFICATION))
                .user(user)
                .title(title)
                .message(message)
                .orderId(orderId)
                .isRead(false)
                .build();
        notificationRepo.save(notification);
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications() {
        User currentUser = userService.getCurrentUserEntity();
        return notificationRepo.findByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(n -> mapper.map(n, NotificationResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepo.save(notification);
    }

    @Override
    public long getUnreadCount() {
        User currentUser = userService.getCurrentUserEntity();
        return notificationRepo.countByUserAndIsReadFalse(currentUser);
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByUserId(String userId) {
        // Admin checking notification status for a specific user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(n -> mapper.map(n, NotificationResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendManualNotification(String userId, String title, String message) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        createNotification(user, title, message, null); // Manual notification might not have an orderId
    }
}
