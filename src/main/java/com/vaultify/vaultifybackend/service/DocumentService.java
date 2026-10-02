package com.vaultify.vaultifybackend.service;

import com.vaultify.vaultifybackend.document.Category;
import com.vaultify.vaultifybackend.entity.Document;
import com.vaultify.vaultifybackend.entity.User;
import com.vaultify.vaultifybackend.repository.DocumentRepository;
import com.vaultify.vaultifybackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private UserRepository userRepository;

    private final Path uploadDir =
            Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

    public DocumentService() {

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create uploads folder",
                    e
            );
        }
    }

    public List<Document> getAllDocuments(
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return repository.findByUser(user);
    }

    public List<Document> getByCategory(
            Category category,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return repository.findByUserAndCategory(
                user,
                category
        );
    }

    public List<Document> searchDocuments(
            String title,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return repository
                .findByUserAndTitleContainingIgnoreCase(
                        user,
                        title
                );
    }

    public Document uploadDocument(
            MultipartFile file,
            String title,
            String description,
            Category category,
            String email) throws IOException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a PDF file."
            );
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null) {
            throw new IllegalArgumentException(
                    "Invalid file name."
            );
        }

        String safeFileName =
                Paths.get(originalFileName)
                        .getFileName()
                        .toString();

        if (!safeFileName
                .toLowerCase()
                .endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "Only PDF files are allowed."
            );
        }

        String storedFileName =
                UUID.randomUUID()
                        + "_"
                        + safeFileName;

        Path targetPath =
                uploadDir
                        .resolve(storedFileName)
                        .normalize();

        if (!targetPath.startsWith(uploadDir)) {
            throw new IllegalArgumentException(
                    "Invalid file path."
            );
        }

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        Document document = new Document();

        document.setTitle(title);
        document.setFileName(safeFileName);
        document.setDescription(description);
        document.setCategory(category);
        document.setFilePath(storedFileName);
        document.setUser(user);

        return repository.save(document);
    }

    public Resource getFile(
            Long id,
            String email) throws IOException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Document document =
                repository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        Path filePath =
                uploadDir
                        .resolve(document.getFilePath())
                        .normalize();

        if (!filePath.startsWith(uploadDir)) {
            throw new IllegalArgumentException(
                    "Invalid file path."
            );
        }

        Resource resource =
                new UrlResource(
                        filePath.toUri()
                );

        if (!resource.exists() ||
                !resource.isReadable()) {

            throw new RuntimeException(
                    "File not found."
            );
        }

        return resource;
    }

    public void deleteDocument(
            Long id,
            String email) throws IOException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Document document =
                repository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        if (document.getFilePath() != null) {

            Path filePath =
                    uploadDir
                            .resolve(
                                    document.getFilePath()
                            )
                            .normalize();

            if (!filePath.startsWith(uploadDir)) {
                throw new IllegalArgumentException(
                        "Invalid file path."
                );
            }

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }

        repository.delete(document);
    }
}