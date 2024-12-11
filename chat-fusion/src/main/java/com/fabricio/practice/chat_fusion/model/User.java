package com.fabricio.practice.chat_fusion.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

// Marks this class as a MongoDB document corresponding to the "users" collection
@Document(collection = "users")
public class User {
	
	// Specifies the unique identifier for this document in the collection
	@Id
	private String id;
	private String username;
	// Creates a unique index on the email field in MongoDB ensuring fast lookups by email and enforcing uniqueness
	@Indexed(unique = true)
	private String email;
	private String password;
	private String pfp;
	
		
	
	// Getters and Setters for the fields
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPfp() {
		return pfp;
	}
	public void setPfp(String pfp) {
		this.pfp = pfp;
	}
	
	
	
}
