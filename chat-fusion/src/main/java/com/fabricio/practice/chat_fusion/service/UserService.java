package com.fabricio.practice.chat_fusion.service;

import java.util.List;

import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.UpdateUserRequest;

// Service interface to define the user related business logic
public interface UserService {

	// Retrieves the profile of an user based on the provided JWT
	public User findUserProfile(String jwt) throws UserException;
	
	// Finds an user by their unique ID
	public User findUserById(String id) throws UserException;
	
	// Finds an user based on a query on their email or username
	public List<User> searchUsers(String query);
	
	// Updates an user information based on the provided ID and update request
	public User updateUser(String id, UpdateUserRequest req)  throws UserException;
	
}