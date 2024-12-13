package com.fabricio.practice.chat_fusion.request;

import java.util.List;

//DTO (Data Transfer Object) for group chat request data
public class GroupChatRequest {
	
	// List of member's IDs
	private List<String> userIds;
	private String chat_name;
	private String chat_image;
	
	// Default no-arguments constructor
	public GroupChatRequest() {
		
	}
	
	// Constructor to create a  GroupChatRequest with the specified fields
	public GroupChatRequest(List<String> userIds, String chat_name, String chat_image) {
		super();
		this.userIds = userIds;
		this.chat_name = chat_name;
		this.chat_image = chat_image;
	}
	
	
	// Getters and Setters for the fields
	public List<String> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}
	public String getChat_name() {
		return chat_name;
	}
	public void setChat_name(String chat_name) {
		this.chat_name = chat_name;
	}
	public String getChat_image() {
		return chat_image;
	}
	public void setChat_image(String chat_image) {
		this.chat_image = chat_image;
	}
	
	
	
	
}
