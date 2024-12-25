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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "ErrorDetail [error=" + error + ", message=" + message + ", timeStamp=" + timeStamp + "]";
	}
	
	
	
	
}
