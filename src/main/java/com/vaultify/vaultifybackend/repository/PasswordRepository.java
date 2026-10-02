package com.vaultify.vaultifybackend.repository;

import com.vaultify.vaultifybackend.entity.PasswordEntry;
import com.vaultify.vaultifybackend.entity.User;
import com.vaultify.vaultifybackend.password.PasswordCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {

    List<PasswordEntry> findByUser(User user);

    List<PasswordEntry> findByUserAndCategory(
            User user,
            PasswordCategory category
    );

    List<PasswordEntry> findByUserAndWebsiteContainingIgnoreCase(
            User user,
            String website
    );

    Optional<PasswordEntry> findByIdAndUser(
            Long id,
            User user
    );
}