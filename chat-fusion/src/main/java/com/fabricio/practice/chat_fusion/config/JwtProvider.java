package com.fabricio.practice.chat_fusion.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Service
public class JwtProvider {

	//Injects the JwtSecretProvider to retrieve the secret key for signing tokens
	@Autowired
	private JwtSecretProvider secretProvider;
	
	
	// Method to create a JWT
	public String generateJwt(Authentication authentication) {
		
		// Retrieves the secret key dynamically from JwtSecretProvider
        SecretKey key = secretProvider.getSecretKey();
		
		// Identifies the entity issuing the token
		String jwt = Jwts.builder().setIssuer("Chat-fusion")
				// Date the token was generated
				.setIssuedAt(new Date())
				// Sets the expiration day to 1 day (24hrs)
				.setExpiration(new Date(new Date().getTime() +86400000 ))
				// Stores the user's email as a claim
				.claim("email", authentication.getName())
				// Signs the token with the secret key
				.signWith(key)
				// Builds and serializes the token into a compact string
				.compact();
		
		return jwt;
	}
	
	// Method to get the email from the JWT
	public String getEmailFromJwt(String jwt) {
		
		// Retrieves the secret key dynamically from JwtSecretProvider
        SecretKey key = secretProvider.getSecretKey();
		
		// Removes the "Bearer" prefix from the JWT
		jwt=jwt.substring(7);
		
		// Parses the JWT, validates it, and extracts the claims (key-value pairs inside the JWT)
		Claims claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
		
		// Extracts the email from the claims
		String email = String.valueOf(claim.get("email"));
		
		return email;
	}
	
}
