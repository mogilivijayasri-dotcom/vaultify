package com.vaultify.vaultifybackend.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM =
            "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;

    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    public EncryptionService() {

        String key =
                System.getenv("VAULTIFY_ENCRYPTION_KEY");

        if (key == null || key.length() != 32) {

            throw new IllegalStateException(
                    "VAULTIFY_ENCRYPTION_KEY must be exactly 32 characters."
            );
        }

        this.secretKey =
                new SecretKeySpec(
                        key.getBytes(StandardCharsets.UTF_8),
                        "AES"
                );
    }

    public String encrypt(String text) {

        try {

            byte[] iv =
                    new byte[IV_LENGTH];

            SecureRandom random =
                    new SecureRandom();

            random.nextBytes(iv);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            byte[] encrypted =
                    cipher.doFinal(
                            text.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String encodedIv =
                    Base64.getEncoder()
                            .encodeToString(iv);

            String encodedData =
                    Base64.getEncoder()
                            .encodeToString(encrypted);

            return encodedIv
                    + ":"
                    + encodedData;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Encryption failed",
                    e
            );
        }
    }

    public String decrypt(
            String encryptedText) {

        try {

            String[] parts =
                    encryptedText.split(":");

            String encodedIv =
                    parts[0];

            String encodedData =
                    parts[1];

            byte[] iv =
                    Base64.getDecoder()
                            .decode(encodedIv);

            byte[] encrypted =
                    Base64.getDecoder()
                            .decode(encodedData);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Decryption failed",
                    e
            );
        }
    }
}