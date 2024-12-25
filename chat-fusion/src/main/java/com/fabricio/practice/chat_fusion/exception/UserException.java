package com.fabricio.practice.chat_fusion.exception;

//Custom exception for user-related errors
public class UserException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor that allows you to create an instance of UserException with a custom message
	public UserException(String message) {
		super(message);
	}
	
}
