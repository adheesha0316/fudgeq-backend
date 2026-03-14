package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.EmailLogResponseDto;
import com.fudgeq.api.entity.EmailLog;
import com.fudgeq.api.entity.Order;
import com.fudgeq.api.repo.EmailLogRepo;
import com.fudgeq.api.repo.OrderRepo;
import com.fudgeq.api.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {
    private final EmailLogRepo emailLogRepo;
    private final OrderRepo orderRepo;
    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EmailLogResponseDto> getAllEmailLogs(int page, int size) {
        return emailLogRepo.findAll(PageRequest.of(page, size, Sort.by("sentAt").descending()))
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailLogResponseDto> getEmailLogsByOrderId(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return emailLogRepo.findByOrder(order).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailLogResponseDto> getFailedEmailLogs(int page, int size) {
        // Note: You might need to add findByIsSuccessFalse in EmailLogRepo
        // return emailLogRepo.findByIsSuccessFalse(PageRequest.of(page, size, Sort.by("sentAt").descending()))
        //        .map(this::convertToDto);
        return Page.empty(); // Placeholder until repo method added
    }

    private EmailLogResponseDto convertToDto(EmailLog log) {
        EmailLogResponseDto dto = mapper.map(log, EmailLogResponseDto.class);
        if (log.getOrder() != null) {
            dto.setOrderId(log.getOrder().getOrderId());
        }
        return dto;
    }
}
