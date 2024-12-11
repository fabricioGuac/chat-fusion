package com.fabricio.practice.chat_fusion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.UserRepository;

// Service for loading user details from the database  for authentication
@Service
public class CustomUserService implements UserDetailsService {
	// User repository to interact with the database
	private UserRepository userRepository;
	
	// Constructor to initialize the user repository
	public CustomUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	// Overrides the loadUserByUsername method to load the user by their email
	// In this case, the 'username' is actually the email because email serves as the unique identifier for the users 
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Fetches the user from the database by their email
		User user = userRepository.findByEmail(email);
		
		// If no user is found throw a UsernameNotFoundException with an appropriate message
		if (user == null) {
			throw new UsernameNotFoundException("User not found with the email: "+ email);
		}
		
		// Creates a list of authorities (roles/permissions) for the user
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		// Returns a new instance of User from Spring Security, passing in the email, password, and authorities
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

}
