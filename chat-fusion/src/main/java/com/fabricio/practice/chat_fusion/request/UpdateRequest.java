package com.fabricio.practice.chat_fusion.request;

import org.springframework.web.multipart.MultipartFile;

// DTO (Data Transfer Object) for updating user or group information
public class UpdateRequest {
	
	// Name to be updated
	private String name;
	// Profile picture to be updated
	private MultipartFile pfp;
	
	// Default no-arguments constructor
	public UpdateRequest() {
		
	}
	
	
	// Parameterized constructor to create an UpdateRequest instance with specific values
	public UpdateRequest(String name, MultipartFile pfp) {
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

	public MultipartFile getPfp() {
		return pfp;
	}

	public void setPfp(MultipartFile pfp) {
		this.pfp = pfp;
	}
}
