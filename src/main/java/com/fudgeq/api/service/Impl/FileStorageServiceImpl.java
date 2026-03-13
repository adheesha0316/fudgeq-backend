package com.fudgeq.api.service.Impl;

import com.fudgeq.api.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
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
    public List<String> storeMultipleFiles(List<MultipartFile> files, String subFolder) {
        List<String> fileUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = storeFile(file, subFolder);
                    if (url != null) fileUrls.add(url);
                }
            }
        }
        return fileUrls;
    }
}
