package ir.aut.secondhand.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

/**
 * Service responsible for storing image files uploaded by users in the advertisement system.
 * Handles file validation, sanitization, and persistent storage with unique filenames
 * to prevent collisions and ensure secure file handling.
 */
@Service
public class ImageStorageService {

    @Value("${app.upload.advertisements-dir}")
    private String uploadDir;

    private Path rootLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    /**
     * Initializes the storage directory upon bean construction.
     * Creates the configured upload directory if it does not already exist.
     *
     * @throws RuntimeException if the directory cannot be created due to I/O errors
     */
    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    /**
     * Stores an uploaded image file to the configured storage directory.
     * Performs validation to ensure the file is not empty and has an allowed MIME type.
     * Generates a unique filename using UUID to prevent naming conflicts and
     * potential path traversal attacks.
     *
     * @param file the multipart file to store
     * @return the generated unique filename that can be used for later retrieval
     * @throws IllegalArgumentException if the file is empty or has an unsupported image format
     * @throws RuntimeException if an I/O error occurs during file storage
     */
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        // Validate the file content type against the allowed image formats
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXTENSIONS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, JPEG, and PNG images are allowed.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            // Determine appropriate file extension based on content type
            if ("image/jpeg".equalsIgnoreCase(contentType) || "image/jpg".equalsIgnoreCase(contentType)) {
                extension = ".jpg";
            } else if ("image/png".equalsIgnoreCase(contentType)) {
                extension = ".png";
            } else {
                throw new IllegalArgumentException("Invalid image format.");
            }

            // Generate unique filename to avoid collisions and ensure security
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            // Copy the uploaded file to the destination with overwrite protection
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}