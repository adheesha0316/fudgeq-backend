package com.fudgeq.api.service;

import com.fudgeq.api.dto.InvoiceResponseDto;
import com.fudgeq.api.entity.Order;
import com.fudgeq.api.enums.InvoiceType;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;

public interface InvoiceService {
    InvoiceResponseDto createInvoice(Order order, InvoiceType type);
    InvoiceResponseDto getInvoiceByOrderIdAndType(String orderId, InvoiceType type);
    ByteArrayInputStream getInvoicePdf(String invoiceId);
    // New methods for Admin functionality
    Page<InvoiceResponseDto> getAllInvoices(int page, int size);
    InvoiceResponseDto getInvoiceById(String invoiceId);
}
