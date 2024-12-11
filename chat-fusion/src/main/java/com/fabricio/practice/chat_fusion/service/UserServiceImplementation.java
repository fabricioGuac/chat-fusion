package com.fabricio.practice.chat_fusion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.config.JwtProvider;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.UserRepository;
import com.fabricio.practice.chat_fusion.request.UpdateUserRequest;

// Implementation of the UserService interface
@Service
public class UserServiceImplementation implements UserService {
	
	// User repository to interact with the database
	private UserRepository userRepository;
	// Dependency for JWT operations
	private JwtProvider jwtProvider;
	
	// Constructor for dependency injection of UserRepository and JwtProvider
	public UserServiceImplementation (UserRepository userRepository, JwtProvider jwtProvider) {
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
	}

	// Retrieves the profile of an user based on the provided JWT
	@Override
	public User findUserProfile(String jwt) throws UserException {
		// Extracts the email from the JWT
		String email = jwtProvider.getEmailFromJwt(jwt);
		
		// Throws an exception if the token is invalid
		if(email == null) {
			throw new BadCredentialsException("Invalid token received");
		} 
		
		// Finds the user by their email
		User user = userRepository.findByEmail(email);
		
		// Throws an exception if the user is not found
		if(user ==null) {
			throw new UserException("User not found with the email " + email);
		}
		
		return user;
		
	}

	// Finds an user by their unique ID
	@Override
	public User findUserById(String id) throws UserException {
		// Queries the database for the user by ID
		Optional<User> optUser = userRepository.findById(id);
		
		// Returns the user if present
		if(optUser.isPresent()) {
			return optUser.get();
		}
		// If the user is not present throws an userException
		throw new UserException("User not found with id " + id);
	}

	
	// Finds an user based on a query on their email or username
	@Override
	public List<User> searchUsers(String query) {
		// Retrieves matching users using the repository
		List<User> users = userRepository.searchUser(query);
		return users;
	}

	// Updates an user information based on the provided ID and update request
	@Override
	public User updateUser(String id, UpdateUserRequest req) throws UserException {
		// Finds the user to update
		User user = findUserById(id);
		
		// Updates the username if it's provided in the request
		if(req.getUsername() != null) {
			user.setUsername(req.getUsername());
		}
		
		// Updates the profile picture if it's provided in the request
		if(req.getPfp() != null) {
			user.setPfp(req.getPfp());
		}
		
		// Saves the updated user to the database
		return userRepository.save(user);
	}

}
