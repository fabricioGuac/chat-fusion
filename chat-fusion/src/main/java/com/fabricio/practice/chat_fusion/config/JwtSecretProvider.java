package com.fabricio.practice.chat_fusion.config;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

// Component that provides the secret key for the JWT signing and validation
@Component
public class JwtSecretProvider {

	// Fetches the secret key from the application properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Converts the raw secret key string into a SecretKey object for cryptographic operations
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
