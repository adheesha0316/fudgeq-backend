package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.AuditLogResponseDto;
import com.fudgeq.api.entity.AuditLog;
import com.fudgeq.api.repo.AuditLogRepo;
import com.fudgeq.api.service.AuditService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditLogRepo auditLogRepo;
    private final CustomIdGenerator idGenerator;
    private final HttpServletRequest request;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public void logAction(String actor, String action, String description, String targetId, boolean isCritical) {
        String auditId = idGenerator.generateNextId(AppConstants.PREFIX_AUDIT);
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuditLog log = AuditLog.builder()
                .auditId(auditId)
                .actor(actor)
                .action(action)
                .description(description)
                .targetId(targetId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isCritical(isCritical)
                .build();

        auditLogRepo.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getAllLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auditLogRepo.findAll(pageable)
                .map(log -> mapper.map(log, AuditLogResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getLogsByActor(String actor, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auditLogRepo.findByActor(actor, pageable)
                .map(log -> mapper.map(log, AuditLogResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getCriticalLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auditLogRepo.findByIsCriticalTrue(pageable)
                .map(log -> mapper.map(log, AuditLogResponseDto.class));
    }
}
