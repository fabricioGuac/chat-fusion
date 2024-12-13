package com.fabricio.practice.chat_fusion.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricio.practice.chat_fusion.config.JwtProvider;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.UserRepository;
import com.fabricio.practice.chat_fusion.request.LoginRequest;
import com.fabricio.practice.chat_fusion.response.AuthResponse;
import com.fabricio.practice.chat_fusion.service.CustomUserService;

// Controller for authentication related routes
@RestController
// Base path for all routes
@RequestMapping("/auth")
public class AuthController {

	// User repository to interact with the database
	private UserRepository userRepository; 
	private PasswordEncoder passwordEncoder;
	private JwtProvider jwtProvider;
	// Custom service to handle user details (authentication)
	private CustomUserService customUserService;
	
	// Constructor to initialize all dependencies via dependency injection
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, CustomUserService customUserService) {
		this.userRepository= userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
		this.customUserService = customUserService;
	}
		
	// Route to handle user signup and authentication
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {
		// Extracts the user details from the request
		String email = user.getEmail();
		String username = user.getUsername();
		String password = user.getPassword();
		
		// Checks if an user with the provided email already exists in the database
		User isUser = userRepository.findByEmail(email);
		
		// If the user exists throw a custom exception to indicate the email is taken
		if(isUser != null) {
			throw new UserException("Email already taken " + email); 
		}
		
		// Creates a new user instance for the signup process
		User newUser = new User();
		
		// Sets the new user's fields with the provided data
		newUser.setEmail(email);
		newUser.setPassword(passwordEncoder.encode(password));
		newUser.setUsername(username);
		
		// Saves the new user to the database
		userRepository.save(newUser);
		
		// Creates an authentication token for the newly created user
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		// Sets the authentication in the security context to indicate the user is logged in
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// Generates the JWT for the authenticated user
		String jwt = jwtProvider.generateJwt(authentication);
		
		// Creates a response including the JWT and a success flag
		AuthResponse res = new AuthResponse(jwt, true);
		
		
		return new ResponseEntity<AuthResponse>(res, HttpStatus.OK);
	}
	
	
	// Route to handle user login and authentication
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) {
	    // Extracts the login details from the request
	    String email = req.getEmail();
	    String password = req.getPassword();
	    
	    try {
	        // Authenticates the user by checking their credentials
	        Authentication authentication = authenticate(email, password);
	        
	        // Sets the authentication in the security context to indicate the user is logged in
	        SecurityContextHolder.getContext().setAuthentication(authentication);        
	        
	        // Generates the JWT for the authenticated user
	        String jwt = jwtProvider.generateJwt(authentication);
	        
	        // Creates a response including the JWT and a success flag
	        AuthResponse res = new AuthResponse(jwt, true);
	        
	        return new ResponseEntity<AuthResponse>(res, HttpStatus.OK);
	    } catch (BadCredentialsException | UsernameNotFoundException e) {
	        // Return a custom error message for either BadCredentials or UsernameNotFound exceptions
	    	 AuthResponse err = new AuthResponse(e.getMessage(), false);
	       
	        return new ResponseEntity<>(err, HttpStatus.UNAUTHORIZED);
	    }
	}

	// Method to authenticate a user based on email and password
	public Authentication authenticate(String email, String password) {
	    // Loads the user details from the custom user service by email
	    UserDetails userDetails = customUserService.loadUserByUsername(email);
	    
	    // If the password does not match the stored password, throw BadCredentialsException
	    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
	        throw new BadCredentialsException("Invalid email or password");
	    }
	    
	    // Returns an authentication token with the user's details and authorities
	    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	
}
