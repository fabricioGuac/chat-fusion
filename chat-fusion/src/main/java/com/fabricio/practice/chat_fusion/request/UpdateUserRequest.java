package com.fabricio.practice.chat_fusion.request;

// DTO (Data Transfer Object) for updating user information
public class UpdateUserRequest {
	
	// Username to be updated
	private String username;
	// Profile picture to be updated
	private String pfp;
	
	
	// Parameterized constructor to create an UpdateUserRequest instance with specific values
	public UpdateUserRequest(String username, String pfp) {
		super();
		this.username = username;
		this.pfp = pfp; 
	}	

	
	// Getters and Setters for the fields
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPfp() {
		return pfp;
	}

	public void setPfp(String pfp) {
		this.pfp = pfp;
	}
}
