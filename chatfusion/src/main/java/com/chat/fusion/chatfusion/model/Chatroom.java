package com.chat.fusion.chatfusion.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

//Marks the class as a MongoDB document and specifies the collection name
@Document(collection = "chatrooms")
//Automatically generates getters, setters, equals, hashCode, and toString methods
@Data
public class Chatroom {
	
	// Maps this field to MongoDB's _id primary key field 
	@Id
	private String chatroomId;
	
	private boolean isGroup;
	
	private String name;
	
	private String groupPfp;
	
	private List<String> adminIds;
	
	private List<String> memberIds;
	
	private String lastMessage;
	
	private Long lastMessageTimestamp;
}
