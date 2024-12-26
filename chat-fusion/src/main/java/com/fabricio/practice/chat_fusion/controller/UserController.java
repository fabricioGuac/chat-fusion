package com.fabricio.practice.chat_fusion.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;
import com.fabricio.practice.chat_fusion.response.ApiResponse;
import com.fabricio.practice.chat_fusion.service.UserService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

//Controller responsible for user-related API endpoints
@RestController
//Base path for all routes
@RequestMapping("/api/users")
public class UserController {
	
	// Service for handling user-related business logic
	private UserService userService;
	
	// Constructor to initialize the user service through dependency injection
	public UserController(UserService userService) {
		this.userService = userService;
	}
	

	// Route to retrieve the authenticated user's profile information
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHandler(@RequestHeader("Authorization") String jwt) throws UserException {
		// Retrieves the user's profile based on the JWT token
		User user = userService.findUserProfile(jwt);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	// Route to search for users by a query parameter
	@GetMapping("/{query}")
	public ResponseEntity<List<User>> searchUserHandler(@PathVariable String query) {
		// Searches for users that match the provided query
		List<User> userList = userService.searchUsers(query);
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}
	
	// Route to update the user's profile information
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateUserHandler(@RequestBody UpdateRequest req, @RequestHeader("Authorization") String jwt) throws UserException, S3Exception, AwsServiceException, SdkClientException, IOException {
		// Retrieves the current user's profile based on the JWT token
		User user = userService.findUserProfile(jwt);
		// Updates the user's information with the data from the request body
		userService.updateUser(user.getId(), req);
		// Creates a response to confirm the update operation was successful
		ApiResponse res = new ApiResponse("User updated successfully", true);
		return new ResponseEntity<ApiResponse>(res, HttpStatus.OK);
	}
	
	
}
