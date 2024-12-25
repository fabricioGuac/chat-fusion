package com.fabricio.practice.chat_fusion.exception;

// Custom exception for message-related errors
public class MessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor that allows you to create an instance of MessageException with a custom message
	public MessageException (String message) {
		super(message);
	}
	
}
