package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLogResponseDto {
    private String logId; // Changed to String
    private String recipientEmail;
    private String subject;
    private LocalDateTime sentAt;
    private boolean isSuccess;
    private String errorMessage;
    private String orderId;
}
