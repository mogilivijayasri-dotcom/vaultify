package com.vaultify.vaultifybackend.repository;

import com.vaultify.vaultifybackend.document.Category;
import com.vaultify.vaultifybackend.entity.Document;
import com.vaultify.vaultifybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {

    List<Document> findByUser(User user);

    List<Document> findByUserAndCategory(
            User user,
            Category category
    );

    List<Document> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title
    );

    Optional<Document> findByIdAndUser(
            Long id,
            User user
    );
}