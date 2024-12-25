package com.fabricio.practice.chat_fusion.exception;

//Custom exception for chat-related errors
public class ChatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor that allows you to create an instance of ChatException with a custom message
	public ChatException(String message) {
		super(message);
	}
	
}
