package com.fudgeq.api.controller;

import com.fudgeq.api.dto.InvoiceResponseDto;
import com.fudgeq.api.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/all")
    public ResponseEntity<Page<InvoiceResponseDto>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(page, size));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponseDto> getInvoiceById(@PathVariable String invoiceId) {
        // You'll need to implement getInvoiceById in InvoiceService
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }
}
