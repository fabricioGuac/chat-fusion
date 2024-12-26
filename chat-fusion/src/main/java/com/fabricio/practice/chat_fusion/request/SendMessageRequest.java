package com.fabricio.practice.chat_fusion.request;

import org.springframework.web.multipart.MultipartFile;

//DTO (Data Transfer Object) for send message request data
public class SendMessageRequest {

	// ID of the chat the message will belong to
	private String chatId;
	// Content of the message
	private String content;
	// File of the message
	private MultipartFile file;
	// Type of message
	private String type;

	
	// Default no-arguments constructor
		SendMessageRequest(){
			
		}
		
	
	// Constructor to create an SendMessageRequest with the specified fields
	public SendMessageRequest(String chatId, String content, MultipartFile file, String type) {
		super();
		this.chatId = chatId;
		this.content = content;
		this.file = file;
		this.type = type;
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
	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getType() {
		// Defaults to text if no type is provided
		return type != null ? type : "text";
	}

	public void setType(String type) {
		this.type = type;
	}
	
}