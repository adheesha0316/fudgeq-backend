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

        // Determine table based on prefix
        if (prefix.equals(AppConstants.PREFIX_USER)) {
            tableName = "users";
            idColumn = "user_id";
        } else if (prefix.contains("PRF")) {
            tableName = "user_profiles";
            idColumn = "profile_id";
        } else if (prefix.contains("AUD")) {
            tableName = "audit_logs";
            idColumn = "audit_id";
        } else {
            throw new IllegalArgumentException("Unknown prefix for ID generation");
        }

        String sql = "SELECT COUNT(" + idColumn + ") FROM " + tableName;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        // If count is null, start from 1, otherwise increment
        long nextNum = (count == null) ? 1 : count + 1;

        // Format: PREFIX + 9-digit padded number (e.g., FQ-USR-000000001)
        return String.format("%s%09d", prefix, nextNum);
    }
}
