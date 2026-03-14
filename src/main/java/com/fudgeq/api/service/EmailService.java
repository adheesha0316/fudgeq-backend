package com.fudgeq.api.service;

import com.fudgeq.api.entity.Invoice;

public interface EmailService {
    void sendInvoiceEmail(Invoice invoice);
}
