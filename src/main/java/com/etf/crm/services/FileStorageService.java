package com.etf.crm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.document.storage.path:documents}")
    private String storageBasePath;

    public String storeFile(byte[] fileContent, String originalFileName, Long contractId) {
        try {
            // Create directory structure: documents/contract_{contractId}/
            Path contractDir = Paths.get(storageBasePath, "contract_" + contractId);
            Files.createDirectories(contractDir);

            // Generate unique filename with timestamp
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;

            // Full file path
            Path filePath = contractDir.resolve(uniqueFileName);

            // Write file to disk
            Files.write(filePath, fileContent, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            // Return relative path for storage in database
            return Paths.get("contract_" + contractId, uniqueFileName).toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalFileName, e);
        }
    }

    public byte[] loadFile(String relativePath) {
        try {
            Path filePath = Paths.get(storageBasePath, relativePath);

            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + relativePath);
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + relativePath, e);
        }
    }

    public void deleteFile(String relativePath) {
        try {
            Path filePath = Paths.get(storageBasePath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

        } catch (IOException e) {
            // Log the error but don't fail the operation
            System.err.println("Failed to delete file: " + relativePath + ", Error: " + e.getMessage());
        }
    }

    public boolean fileExists(String relativePath) {
        Path filePath = Paths.get(storageBasePath, relativePath);
        return Files.exists(filePath);
    }

    public long getFileSize(String relativePath) {
        try {
            Path filePath = Paths.get(storageBasePath, relativePath);
            return Files.size(filePath);
        } catch (IOException e) {
            return 0;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}