package com.fudgeq.api.controller;

import com.fudgeq.api.dto.InvoiceResponseDto;
import com.fudgeq.api.enums.InvoiceType;
import com.fudgeq.api.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@CrossOrigin
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<InvoiceResponseDto> getMyInvoice(
            @PathVariable String orderId,
            @RequestParam InvoiceType type) {
        return ResponseEntity.ok(invoiceService.getInvoiceByOrderIdAndType(orderId, type));
    }

    @GetMapping("/download/{invoiceId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<InputStreamResource> downloadMyInvoice(@PathVariable String invoiceId) {
        ByteArrayInputStream pdfStream = invoiceService.getInvoicePdf(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice-" + invoiceId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
