package com.fudgeq.api.service.Impl;

import com.fudgeq.api.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public String storeFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) return null;

        try {
            Path targetLocation = Paths.get(uploadDir).resolve(subFolder).toAbsolutePath().normalize();
            if (!Files.exists(targetLocation)) {
                Files.createDirectories(targetLocation);
            }

            // Fixed: Removed unnecessary .toString() call as the + operator handles it
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = targetLocation.resolve(fileName);

            // Improvement: Added REPLACE_EXISTING to prevent issues during rare collisions
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + subFolder + "/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public String storeFile(ByteArrayInputStream inputStream, String fileName, String subFolder) {
        try {
            Path uploadPath = Paths.get("uploads").resolve(subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            return subFolder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store generated PDF file", e);
        }
    }

    @Override
    public ByteArrayInputStream loadFileAsResource(String filePath) {
        try {
            Path path = Paths.get("uploads").resolve(filePath).normalize();
            byte[] data = Files.readAllBytes(path);
            return new ByteArrayInputStream(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + filePath, e);
        }
    }

    @Override
    public List<String> storeMultipleFiles(List<MultipartFile> files, String subFolder) {
        List<String> filePaths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String path = storeFile(file, subFolder);
                if (path != null) filePaths.add(path);
            }
        }
        return filePaths;
    }

    @Override
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return;
        try {
            Path filePath = Paths.get(uploadDir).resolve(relativePath).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", relativePath, ex);
        }
    }

    @Override
    public void deleteMultipleFiles(List<String> relativePaths) {
        if (relativePaths != null) {
            relativePaths.forEach(this::deleteFile);
        }
    }

    @Override
    public String getFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return null;
        return baseUrl + relativePath;
    }
}
