package com.fabricio.practice.chat_fusion.request;

//DTO (Data Transfer Object) for edit message request data
public class EditMessageRequest {

	// New content for the message
	private String newContent;
	// ID of the message to be edited
	private String messageId;
	
	
	// Default no-arguments constructor
	public EditMessageRequest() {
		
	}


	// Constructor to create an EditMessageRequest with the specified fields
	public EditMessageRequest(String newContent, String messageId) {
		super();
		this.newContent = newContent;
		this.messageId = messageId;
	}

	// Getters and Setters for the fields
	public String getNewContent() {
		return newContent;
	}


	public void setNewContent(String newContent) {
		this.newContent = newContent;
	}


	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	
	
	
}
