package com.fabricio.practice.chat_fusion.exception;

//Custom exception for chat-related errors
public class ChatException extends Exception {
	
	// Constructor that allows you to create an instance of ChatException with a custom message
	public ChatException(String message) {
		super(message);
	}
	
}
