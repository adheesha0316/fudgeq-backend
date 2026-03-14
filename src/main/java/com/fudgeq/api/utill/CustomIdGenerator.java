package com.fudgeq.api.utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomIdGenerator {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Generates a unique, thread-safe custom ID based on a centralized sequence table.
     * Use Propagation.REQUIRES_NEW to ensure ID generation is not rolled back by parent transaction failures.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateNextId(String prefix) {
        try {
            // 1. Fetch and Lock the row for the specific prefix to handle concurrency
            String selectSql = "SELECT next_val FROM id_sequences WHERE sequence_name = ? FOR UPDATE";

            Long currentVal = jdbcTemplate.queryForObject(selectSql, Long.class, prefix);
            long nextVal = (currentVal == null) ? 1 : currentVal;

            // 2. Update the sequence table for the next caller
            String updateSql = "UPDATE id_sequences SET next_val = next_val + 1 WHERE sequence_name = ?";
            jdbcTemplate.update(updateSql, prefix);

            // 3. Format and return the ID
            return String.format("%s%09d", prefix, nextVal);

        } catch (Exception e) {
            log.error("Failed to generate custom ID for prefix: {}. Error: {}", prefix, e.getMessage());
            throw new RuntimeException("ID Generation Error: Could not generate unique ID for " + prefix);
        }
    }
}
