package com.fabricio.practice.chat_fusion.response;

// Response model for API operations, carrying a message and a status flag
public class ApiResponse {
	// A message to describe the result of the operation
	private String message;
	// A status flag indicating the success or failure of the operation
	private boolean status;
	
	// Parameterized constructor to initialize the message and status fields
	public ApiResponse(String message, boolean status) {
		super();
		this.message = message;
		this.status = status;
	}

	// Getter methods for the response fields
	public String getMessage() {
		return message;
	}

	public boolean isStatus() {
		return status;
	}

	
	
}
