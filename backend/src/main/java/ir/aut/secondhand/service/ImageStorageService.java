package ir.aut.secondhand.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.advertisements-dir}")
    private String uploadDir;

    private Path rootLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXTENSIONS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, JPEG, and PNG images are allowed.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}