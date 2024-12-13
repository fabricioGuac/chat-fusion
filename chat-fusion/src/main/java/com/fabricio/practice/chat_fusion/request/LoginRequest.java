package com.fabricio.practice.chat_fusion.request;

// DTO (Data Transfer Object) for login request data
public class LoginRequest {
	
	// Email provided for login
	private String email;
	// Password provided for login
	private String password;
	
	// Default no-arguments constructor
	public LoginRequest() {
		
	}
	
	// Constructor to create a login request with the specified fields
	public LoginRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	
	// Getters and Setters for the fields
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
	
}
