package com.fudgeq.api.service.Impl;

import com.fudgeq.api.entity.EmailLog;
import com.fudgeq.api.entity.Invoice;
import com.fudgeq.api.repo.EmailLogRepo;
import com.fudgeq.api.service.EmailService;
import com.fudgeq.api.service.FileStorageService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final FileStorageService fileStorageService;
    private final EmailLogRepo emailLogRepo;
    private final CustomIdGenerator idGenerator;

    @Override
    @Async
    public void sendInvoiceEmail(Invoice invoice) {
        boolean success = false;
        String errorMessage = null;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(invoice.getOrder().getUser().getEmail());
            helper.setSubject("FudgeQ Invoice - " + invoice.getInvoiceNumber());
            helper.setText("<h3>Hi " + invoice.getOrder().getUser().getFirstName() + ",</h3>" +
                    "<p>Please find your attached invoice for order " + invoice.getOrder().getOrderId() + ".</p>", true);

            ByteArrayInputStream pdfStream = fileStorageService.loadFileAsResource(invoice.getPdfFilePath());
            helper.addAttachment(invoice.getInvoiceNumber() + ".pdf", new InputStreamResource(pdfStream));

            mailSender.send(message);
            success = true;
            log.info("Email sent successfully to {}", invoice.getOrder().getUser().getEmail());

        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("Failed to send email: {}", errorMessage);
        } finally {
            saveEmailLog(invoice, success, errorMessage);
        }
    }

    private void saveEmailLog(Invoice invoice, boolean success, String error) {
        // Generating the String ID using your CustomIdGenerator
        String logId = idGenerator.generateNextId(AppConstants.PREFIX_EMAIL_LOG);

        EmailLog emailLog = EmailLog.builder()
                .logId(logId)
                .recipientEmail(invoice.getOrder().getUser().getEmail())
                .subject("Invoice Email - " + invoice.getInvoiceNumber())
                .sentAt(LocalDateTime.now())
                .isSuccess(success)
                .errorMessage(error)
                .order(invoice.getOrder())
                .build();

        emailLogRepo.save(emailLog);
    }
}
