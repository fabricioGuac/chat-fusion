package com.fabricio.practice.chat_fusion.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.fabricio.practice.chat_fusion.model.User;

// Repository interface for interacting with the "users" collection in MongoDB
@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	// Finds a user by their email  
	public User findByEmail(String email);
	
	// Searches for users by matching their username or email using a case-insensitive regular expression
	@Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
	public List<User> searchUser(String query);
	
}
