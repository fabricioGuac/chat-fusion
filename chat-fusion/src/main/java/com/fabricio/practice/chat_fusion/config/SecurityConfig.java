package com.fabricio.practice.chat_fusion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

//Tells Spring that this class contains configuration for the application context
@Configuration
public class SecurityConfig {
	
	
	private final JwtSecretProvider secretProvider;

    // Injects JwtSecretProvider via constructor
    public SecurityConfig(JwtSecretProvider secretProvider) {
        this.secretProvider = secretProvider;
    }
	

	// Defines a bean for configuring HTTP security, including session management, authentication, CORS and CSRF protection 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            //  Configures session management to be stateless, meaning no HTTP session will be created
        	// Since JWT authentication will be used there us no need to store user sessions on the server
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ) 
            .authorizeHttpRequests(authz ->
                authz
                // Requires authentication for all requests that match  "/api/**"
                    .requestMatchers("/api/**").authenticated() 
                // Allows all other requests without requiring authentication
                    .anyRequest().permitAll()
            )
            // Adds custom JWT filter before the BasicAuthenticationFilte
            .addFilterBefore(new JwtValidator(secretProvider), BasicAuthenticationFilter.class)
            // Disables Cross-Site Request Forgery (CSRF) protection
            // CSRF is not needed when using JWT as authentication is handled via headers and not cookies
            .csrf(csrf -> csrf.disable())
            // Configures Cross-Origin Resource Sharing to handle requests from specific origins
            .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                    CorsConfiguration config = new CorsConfiguration();
                    // Allows requests from the development front end (http://localhost:3000)
                    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                    // Allows all HTTP methods
                    config.setAllowedMethods(Collections.singletonList("*"));
                    // Allows cookies or credentials to be sent along with the request
                    config.setAllowCredentials(true);
                    // Exposes the Authorization header to the front end (important for handling JWT tokens)
                    config.setExposedHeaders(Arrays.asList("Authorization"));
                    // Caches the CORS preflight request for 1 hour
                    config.setMaxAge(3600L);
                    
                    return config;
                }
            }))
            // Disables the default form-based login (not needed in API-based authentication scenarios like JWT)
            .formLogin(login -> login.disable())
            // Disables basic HTTP authentication, since JWT will be used for authentication instead
            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    // Use BCryptPasswordEncoder for password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


