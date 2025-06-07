package com.fabricio.practice.chat_fusion.request;

// DTO (Data Transfer Object) for call requests
public class CallRequest {
	// Id of the chat from where the call is comming from
	private String chatId;
	// Id of the user making the call
	private String userId;
	// Name of the user or group that is making the call
	private String displayName;
	
	// Default no-arguments constructor
	public CallRequest() {
		
	}

	// Constructor to create a CallRequest with the specified fields
	public CallRequest(String chatId,String userId, String displayName) {
		super();
		this.chatId = chatId;
		this.userId = userId;
		this.displayName = displayName;
	}
	
	// Getters and Setters
	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getUserId() {
		return userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}	
}
