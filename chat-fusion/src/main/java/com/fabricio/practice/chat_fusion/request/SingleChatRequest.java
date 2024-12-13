package com.fabricio.practice.chat_fusion.request;

//DTO (Data Transfer Object) for single chat request data
public class SingleChatRequest {
	
	// ID of the target user for one to one chat
	private String userId;
	
	// Default no-arguments constructor
	public SingleChatRequest() {
		
	}

	// Constructor to create a  GroupChatRequest with the specified fields
	public SingleChatRequest(String userId) {
		super();
		this.userId = userId;
	}

	// Getters and Setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	
}
