package com.fudgeq.api.repo;

import com.fudgeq.api.entity.EmailLog;
import com.fudgeq.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepo extends JpaRepository<EmailLog, String> {
    List<EmailLog> findByOrder(Order order);
}
