package com.vaultify.vaultifybackend.service;

import com.vaultify.vaultifybackend.dto.LoginResponse;
import com.vaultify.vaultifybackend.entity.User;
import com.vaultify.vaultifybackend.repository.UserRepository;
import com.vaultify.vaultifybackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Signup
    public User saveUser(User user) {

        String hashedPassword =
                passwordEncoder.encode(user.getPassword());

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Login
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user != null &&
                passwordEncoder.matches(password, user.getPassword())) {

            String token = jwtService.generateToken(user.getEmail());

            return new LoginResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }

        return null;
    }
}