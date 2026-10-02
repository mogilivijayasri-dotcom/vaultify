package com.vaultify.vaultifybackend.controller;

import com.vaultify.vaultifybackend.dto.LoginResponse;
import com.vaultify.vaultifybackend.entity.User;
import com.vaultify.vaultifybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Signup
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // Get all users
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    // Login
    @GetMapping("/login")
    public LoginResponse login(@RequestParam String email,
                               @RequestParam String password) {

        return userService.login(email, password);
    }
}