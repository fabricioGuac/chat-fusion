package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.config.JwtProvider;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.UserRepository;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Implementation of the UserService interface
@Service
public class UserServiceImplementation implements UserService {
	
	// User repository to interact with user data in the database
	private UserRepository userRepository;
	// Dependency for JWT operations
	private JwtProvider jwtProvider;
	// AWS Service to interact with the S3 bucket
	private AwsService awsS3Client;
	
	// Constructor for dependency injection of UserRepository, JwtProvider and AwsServide
	public UserServiceImplementation (UserRepository userRepository, JwtProvider jwtProvider, AwsService awsS3Client) {
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.awsS3Client = awsS3Client;
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
	public User updateUser(String id, UpdateRequest req) throws UserException, S3Exception, AwsServiceException, SdkClientException, IOException {
	    // Finds the user to update
	    User user = findUserById(id);

	    // Update the username if provided and valid
	    if (req.getName() != null && !req.getName().isBlank()) {
	        String updatedUsername = req.getName().trim();
	        if (updatedUsername.length() > 50) {
	            throw new UserException("Username must not exceed 50 characters");
	        }
	        user.setUsername(updatedUsername);
	    }

	    // Update the profile picture if provided
	    if (req.getPfp() != null) {
	    	String awsUrl = awsS3Client.uploadFile(id+"/pfp", req.getPfp().getInputStream(), req.getPfp().getContentType());
	        
	    	if(user.getPfp() == null || user.getPfp().isBlank()) {
	    		
	    		user.setPfp(awsUrl);
	    	}
	    }

	    // Save the updated user to the database
	    return userRepository.save(user);
	}

	// Updates the user's last connection
	@Override
	public void updateLastConnection(String email, Instant lastConnection) throws UserException {
	    // Finds the user by email
	    User user = userRepository.findByEmail(email);

	    // Ensures the user exists before proceeding
	    if (user == null) {
	        throw new UserException("User not found with email: " + email);
	    }

	    // Updates the user's last connection timestamp
	    user.setLastconnection(lastConnection);

	    // Saves the updated user to the database
	    userRepository.save(user);
	}

	// Fetches the user last connection
	@Override
	public Instant getLastConnection(String id) throws UserException {
		// Finds the user to get their last connection
		User user = findUserById(id);
		// Returns the user last connection
		return user.getLastConnection();
	}

}
