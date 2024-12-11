package com.fabricio.practice.chat_fusion.exception;

import java.time.LocalDateTime;

// Class to represent the structure of an error response to be send to the client if an exception occurs
public class ErrorDetail {

	// The error type
	private String error;
	// A message explaining the error
	private String message;
	// The time stamp when the error occurred
	private LocalDateTime timeStamp;
	
	// Default constructor, used for serialization or other frameworks
	public ErrorDetail() {
		
	}
	
	// Parameterized constructor to create an ErrorDetail instance with specific values
	public ErrorDetail(String error, String message, LocalDateTime timeStamp) {
		super();
		this.error = error;
		this.message = message;
		this.timeStamp = timeStamp;
	}
	
}
