package com.fudgeq.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subFolder);
    // Add this for system-generated PDFs (Invoices)
    String storeFile(ByteArrayInputStream inputStream, String fileName, String subFolder);
    ByteArrayInputStream loadFileAsResource(String filePath);
    List<String> storeMultipleFiles(List<MultipartFile> files, String subFolder);
    void deleteFile(String relativePath);
    void deleteMultipleFiles(List<String> relativePaths);
    String getFullUrl(String relativePath);
}
