package com.fudgeq.api.dto;

import com.fudgeq.api.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDto {
    private String invoiceId;
    private String invoiceNumber;
    private String orderId;
    private LocalDateTime generatedAt;
    private String pdfUrl;
    private InvoiceType invoiceType;
}
