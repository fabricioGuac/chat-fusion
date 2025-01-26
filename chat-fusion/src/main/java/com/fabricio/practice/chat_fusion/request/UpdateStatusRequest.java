package com.fabricio.practice.chat_fusion.request;

//DTO (Data Transfer Object) for updating online status request
public class UpdateStatusRequest {
	// Email of the user whose status will be updated
	private String email;
	// Boolean to track if the user is online or not
	private boolean online;
	// ID of the user to add to the chat connectedUsers field
	private String userId;
	
	// Default no-arguments constructor
	public UpdateStatusRequest() {

	}
	
	// Constructor to create a UpdateStatusRequest with the specified fields
	public UpdateStatusRequest(String email, boolean online, String userId) {
		super();
		this.email = email;
		this.online = online;
		this.userId = userId;
	}
	
	// Getters and setters for the fields
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	public String getUserId() {
	    return userId;
	}

	public void setUserId(String userId) {
	    this.userId = userId;
	}
	
}
