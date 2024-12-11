package com.fabricio.practice.chat_fusion.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Class to validate the JWT for every incoming request
public class JwtValidator extends OncePerRequestFilter {
	
//	// Injects the JwtSecretProvider to retrieve the secret key for token validation
//	@Autowired
//	private JwtSecretProvider secretProvider;
	
	private final JwtSecretProvider secretProvider;

    // Constructor to inject JwtSecretProvider
    public JwtValidator(JwtSecretProvider secretProvider) {
        this.secretProvider = secretProvider;
    }

	// Method to process the JWT validation for incoming requests	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 	Retrieves the JWT from the request's authentication header	
		String jwt = request.getHeader("Authorization");
	
		// Verifies if the token is present in the request		
		if(jwt != null) {
			try {
				// Removes the "Bearer" prefix from the JWT
				jwt= jwt.substring(7);
				
				// Fetches the secret key from JwtSecretProvide
				SecretKey key = secretProvider.getSecretKey();
				// Parses the JWT, validates it, and extracts the claims (key-value pairs inside the JWT)
				Claims claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
				
				// Extracts the email from the claims
				String email = String.valueOf(claim.get("email"));
				// Extracts the authorities (roles/permissions) from the claims
				String authorities = String.valueOf(claim.get("authorities"));
				
				// Converts the comma separated string of authorities into a list of GrantedAuthority objects, which are needed to represent roles or privileges in Spring Security 
				List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
				
				// Creates a new Authentication object with the email and authorities
				Authentication authentication = new UsernamePasswordAuthenticationToken(email,null, auths); 
				
				// Sets the Authentication object into the security context, making it available globally
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				// Handles exceptions such as invalid tokens
				throw new BadCredentialsException ("Invalid token received");
			}
		}
		// Proceeds to the next filter in the chain
		filterChain.doFilter(request, response);
		
	}

}
