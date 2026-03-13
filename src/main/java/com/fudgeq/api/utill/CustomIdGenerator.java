package com.fudgeq.api.utill;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomIdGenerator {

    private final JdbcTemplate jdbcTemplate;

    public String generateNextId(String prefix) {
        String tableName;
        String idColumn;

        if (prefix.equals(AppConstants.PREFIX_USER)) {
            tableName = "users";
            idColumn = "user_id";
        } else if (prefix.equals(AppConstants.PREFIX_PROFILE)) {
            tableName = "user_profiles";
            idColumn = "profile_id";
        } else if (prefix.equals(AppConstants.PREFIX_AUDIT)) {
            tableName = "audit_logs";
            idColumn = "log_id"; // Changed to match your AuditLog field logId
        } else {
            throw new IllegalArgumentException("Unknown prefix for ID generation: " + prefix);
        }

        String sql = "SELECT COUNT(" + idColumn + ") FROM " + tableName;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        long nextNum = (count == null) ? 1 : count + 1;

        return String.format("%s%09d", prefix, nextNum);
    }
}
