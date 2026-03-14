package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.InvoiceResponseDto;
import com.fudgeq.api.entity.Invoice;
import com.fudgeq.api.entity.Order;
import com.fudgeq.api.enums.InvoiceType;
import com.fudgeq.api.repo.InvoiceRepo;
import com.fudgeq.api.repo.OrderRepo;
import com.fudgeq.api.service.FileStorageService;
import com.fudgeq.api.service.InvoiceService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepo invoiceRepo;
    private final OrderRepo orderRepo;
    private final CustomIdGenerator idGenerator;
    private final FileStorageService fileStorageService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public InvoiceResponseDto createInvoice(Order order, InvoiceType type) {
        invoiceRepo.findByOrderAndInvoiceType(order, type).ifPresent(i -> {
            throw new RuntimeException(type + " invoice already exists for this order");
        });

        String invoiceId = idGenerator.generateNextId(AppConstants.PREFIX_INVOICE);
        String invoiceNum = (type == InvoiceType.PAYMENT ? "PAY-" : "ORD-") + System.currentTimeMillis();
        String fileName = invoiceNum + ".pdf";

        // Generate PDF and Store it
        ByteArrayInputStream pdfStream = generatePdfContent(order, invoiceNum, type);
        String filePath = fileStorageService.storeFile(pdfStream, fileName, "invoices");

        Invoice invoice = Invoice.builder()
                .invoiceId(invoiceId)
                .order(order)
                .invoiceNumber(invoiceNum)
                .generatedAt(LocalDateTime.now())
                .invoiceType(type)
                .pdfFilePath(filePath)
                .build();

        Invoice saved = invoiceRepo.save(invoice);
        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoiceByOrderIdAndType(String orderId, InvoiceType type) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Invoice invoice = invoiceRepo.findByOrderAndInvoiceType(order, type)
                .orElseThrow(() -> new RuntimeException(type + " invoice not found for this order"));

        return convertToDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream getInvoicePdf(String invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return fileStorageService.loadFileAsResource(invoice.getPdfFilePath());
    }

    private InvoiceResponseDto convertToDto(Invoice invoice) {
        InvoiceResponseDto dto = mapper.map(invoice, InvoiceResponseDto.class);
        dto.setOrderId(invoice.getOrder().getOrderId());
        dto.setPdfUrl(fileStorageService.getFullUrl(invoice.getPdfFilePath()));
        return dto;
    }

    private ByteArrayInputStream generatePdfContent(Order order, String invoiceNum, InvoiceType type) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("FudgeQ - " + type + " INVOICE").setBold().setFontSize(20));
        document.add(new Paragraph("Invoice Number: " + invoiceNum));
        document.add(new Paragraph("Order ID: " + order.getOrderId()));
        document.add(new Paragraph("Customer: " + order.getUser().getFirstName() + " " + order.getUser().getLastName()));

        // Add your Table logic here as we did before...

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
