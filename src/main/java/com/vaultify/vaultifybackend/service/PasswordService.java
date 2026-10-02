package com.vaultify.vaultifybackend.service;

import com.vaultify.vaultifybackend.entity.PasswordEntry;
import com.vaultify.vaultifybackend.entity.User;
import com.vaultify.vaultifybackend.password.PasswordCategory;
import com.vaultify.vaultifybackend.repository.PasswordRepository;
import com.vaultify.vaultifybackend.repository.UserRepository;
import com.vaultify.vaultifybackend.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordService {

    @Autowired
    private PasswordRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    public PasswordEntry savePassword(
            PasswordEntry entry,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (entry.getPassword() == null ||
                entry.getPassword().isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        String encryptedPassword =
                encryptionService.encrypt(entry.getPassword());

        entry.setPassword(encryptedPassword);
        entry.setUser(user);

        return repository.save(entry);
    }

    public List<PasswordEntry> getAllPasswords(
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return repository.findByUser(user);
    }

    public List<PasswordEntry> getByCategory(
            PasswordCategory category,
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

    public List<PasswordEntry> searchWebsite(
            String website,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return repository.findByUserAndWebsiteContainingIgnoreCase(
                user,
                website
        );
    }

    public PasswordEntry getPassword(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        PasswordEntry entry =
                repository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Password not found"
                                )
                        );

        String decryptedPassword =
                encryptionService.decrypt(
                        entry.getPassword()
                );

        entry.setPassword(decryptedPassword);

        return entry;
    }

    public void deletePassword(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        PasswordEntry entry =
                repository.findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Password not found"
                                )
                        );

        repository.delete(entry);
    }
}