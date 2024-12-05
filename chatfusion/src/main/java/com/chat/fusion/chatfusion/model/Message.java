package com.chat.fusion.chatfusion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

//Marks the class as a MongoDB document and specifies the collection name
@Document(collection = "messages")
//Automatically generates getters, setters, equals, hashCode, and toString methods
@Data
public class Message {

	// Maps this field to MongoDB's _id primary key field
	@Id
	private String messageId;
	
	private String chatroomId;
	
	private String senderId;
	
	private Long timestamp;
	
	private String type;
	
}
