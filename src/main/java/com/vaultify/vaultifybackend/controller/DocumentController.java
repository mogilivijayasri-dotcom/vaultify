package com.vaultify.vaultifybackend.controller;

import com.vaultify.vaultifybackend.document.Category;
import com.vaultify.vaultifybackend.entity.Document;
import com.vaultify.vaultifybackend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService service;

    @GetMapping
    public List<Document> getAll(
            Principal principal) {

        return service.getAllDocuments(
                principal.getName()
        );
    }

    @GetMapping("/category")
    public List<Document> getCategory(
            @RequestParam Category category,
            Principal principal) {

        return service.getByCategory(
                category,
                principal.getName()
        );
    }

    @GetMapping("/search")
    public List<Document> search(
            @RequestParam String title,
            Principal principal) {

        return service.searchDocuments(
                title,
                principal.getName()
        );
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Document upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(
                    value = "description",
                    defaultValue = ""
            ) String description,
            @RequestParam("category") Category category,
            Principal principal
    ) throws IOException {

        return service.uploadDocument(
                file,
                title,
                description,
                category,
                principal.getName()
        );
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            Principal principal) throws IOException {

        Resource resource =
                service.getFile(
                        id,
                        principal.getName()
                );

        return ResponseEntity.ok()
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() +
                                "\""
                )
                .body(resource);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            Principal principal)
            throws IOException {

        service.deleteDocument(
                id,
                principal.getName()
        );

        return "Document Deleted Successfully";
    }
}