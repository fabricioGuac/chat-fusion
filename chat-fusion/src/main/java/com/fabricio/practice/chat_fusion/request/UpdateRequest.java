package com.fabricio.practice.chat_fusion.request;

// DTO (Data Transfer Object) for updating user or group information
public class UpdateRequest {
	
	// Name to be updated
	private String name;
	// Profile picture to be updated
	private String pfp;
	
	// Default no-arguments constructor
	public UpdateRequest() {
		
	}
	
	
	// Parameterized constructor to create an UpdateRequest instance with specific values
	public UpdateRequest(String name, String pfp) {
		super();
		this.name = name;
		this.pfp = pfp; 
	}	

	
	// Getters and Setters for the fields

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getPfp() {
		return pfp;
	}

	public void setPfp(String pfp) {
		this.pfp = pfp;
	}
}
