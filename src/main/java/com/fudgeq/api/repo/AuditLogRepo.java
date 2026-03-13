package com.fudgeq.api.repo;

import com.fudgeq.api.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepo extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findByActor(String actor, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
    Page<AuditLog> findByIsCriticalTrue(Pageable pageable);

    // Prevent deletion by overriding delete methods to throw exceptions
    @Override
    default void deleteById(String id) {
        throw new UnsupportedOperationException("Audit logs cannot be deleted.");
    }

    @Override
    default void delete(AuditLog entity) {
        throw new UnsupportedOperationException("Audit logs cannot be deleted.");
    }

    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException("Audit logs cannot be deleted.");
    }
}
