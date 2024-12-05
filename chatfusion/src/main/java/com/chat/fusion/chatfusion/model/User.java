package com.chat.fusion.chatfusion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

// Marks the class as a MongoDB document and specifies the collection name
@Document(collection = "users")
//Automatically generates getters, setters, equals, hashCode, and toString methods
@Data
public class User {
	
	// Maps this field to MongoDB's _id primary key field 	
	@Id
	private String userId;
	
	// Ensures there are no repeated email directions	
	@Indexed(unique = true)
	private String email;
	
	private String password;
	
	private String username;
	
	private  String pfp; 
	
	private Integer unreadMessages;
}
