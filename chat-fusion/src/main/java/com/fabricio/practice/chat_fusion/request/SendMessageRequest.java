package com.fabricio.practice.chat_fusion.request;

//DTO (Data Transfer Object) for send message request data
public class SendMessageRequest {

	// ID of the chat the message will belong to
	private String chatId;
	// Content of the message
	private String content;
	
	// Default no-arguments constructor
	SendMessageRequest(){
		
	}
	
	// Constructor to create an SendMessageRequest with the specified fields
	public SendMessageRequest( String chatId, String content) {
		super();
		this.chatId = chatId;
		this.content = content;
	}

	//Getters and Setters for the fields
	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}