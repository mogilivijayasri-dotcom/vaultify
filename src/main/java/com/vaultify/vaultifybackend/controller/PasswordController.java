package com.vaultify.vaultifybackend.controller;

import com.vaultify.vaultifybackend.entity.PasswordEntry;
import com.vaultify.vaultifybackend.password.PasswordCategory;
import com.vaultify.vaultifybackend.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/passwords")
public class PasswordController {

    @Autowired
    private PasswordService service;

    @PostMapping("/save")
    public PasswordEntry save(
            @RequestBody PasswordEntry entry,
            Principal principal) {

        return service.savePassword(
                entry,
                principal.getName()
        );
    }

    @GetMapping
    public List<PasswordEntry> getAll(
            Principal principal) {

        return service.getAllPasswords(
                principal.getName()
        );
    }

    @GetMapping("/category")
    public List<PasswordEntry> category(
            @RequestParam PasswordCategory category,
            Principal principal) {

        return service.getByCategory(
                category,
                principal.getName()
        );
    }

    @GetMapping("/search")
    public List<PasswordEntry> search(
            @RequestParam String website,
            Principal principal) {

        return service.searchWebsite(
                website,
                principal.getName()
        );
    }

    @GetMapping("/{id}/view")
    public PasswordEntry viewPassword(
            @PathVariable Long id,
            Principal principal) {

        return service.getPassword(
                id,
                principal.getName()
        );
    }

    @DeleteMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            Principal principal) {

        service.deletePassword(
                id,
                principal.getName()
        );

        return "Password Deleted Successfully";
    }
}