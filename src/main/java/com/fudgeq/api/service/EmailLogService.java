package com.fudgeq.api.service;

import com.fudgeq.api.dto.EmailLogResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmailLogService {
    // Get all logs for admin with pagination
    Page<EmailLogResponseDto> getAllEmailLogs(int page, int size);

    // Get logs specific to an order
    List<EmailLogResponseDto> getEmailLogsByOrderId(String orderId);

    // Get only failed logs for monitoring
    Page<EmailLogResponseDto> getFailedEmailLogs(int page, int size);
}
