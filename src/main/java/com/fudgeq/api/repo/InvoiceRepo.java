package com.fudgeq.api.repo;

import com.fudgeq.api.entity.Invoice;
import com.fudgeq.api.entity.Order;
import com.fudgeq.api.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, String> {
    Optional<Invoice> findByOrderAndInvoiceType(Order order, InvoiceType type);
    List<Invoice> findByOrder(Order order);
}
