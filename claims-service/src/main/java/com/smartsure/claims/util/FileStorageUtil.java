package com.smartsure.claims.util;

import com.smartsure.claims.exception.InvalidOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    public FileStorageUtil(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new InvalidOperationException("Uploaded file is empty");
        }

        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidOperationException("Only PDF, PNG and JPG files are allowed");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalName.replaceAll("\\s+", "_");

        Path target = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return target.toString();
    }
}