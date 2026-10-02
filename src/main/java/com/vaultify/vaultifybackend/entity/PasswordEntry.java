package com.vaultify.vaultifybackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaultify.vaultifybackend.password.PasswordCategory;
import jakarta.persistence.*;

@Entity
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String website;

    private String username;

    private String password;

    private String notes;

    @Enumerated(EnumType.STRING)
    private PasswordCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public PasswordEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PasswordCategory getCategory() {
        return category;
    }

    public void setCategory(PasswordCategory category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}