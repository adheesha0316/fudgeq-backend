package com.fudgeq.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subFolder);
    List<String> storeMultipleFiles(List<MultipartFile> files, String subFolder);
    void deleteFile(String relativePath);
    void deleteMultipleFiles(List<String> relativePaths);
    String getFullUrl(String relativePath);
}
