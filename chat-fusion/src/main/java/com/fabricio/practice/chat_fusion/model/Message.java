package com.fabricio.practice.chat_fusion.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//Marks this class as a MongoDB document corresponding to the "messages" collection
@Document(collection = "messages")
public class Message {
	
	// Specifies the unique identifier for this document in the collection
	@Id
	private String id;
	private String content;
	private LocalDateTime timestamp;
	
	private User user;
	private Chat chat;
	
	// No-args constructor for serialization and deserialization frameworks
	public Message() {
		
	}

	// Full-args constructor for initializing all fields of the Message object
	public Message(String id, String content, LocalDateTime timestamp, User user, Chat chat) {
		super();
		this.id = id;
		this.content = content;
		this.timestamp = timestamp;
		this.user = user;
		this.chat = chat;
	}

	
	// Getters and Setters for the fields
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	// To string method
	@Override
	public String toString() {
	    return "Message [id=" + id + ", content=" + content + ", timestamp=" + timestamp + "]";
	}

	// Hash code method
	@Override
	public int hashCode() {
	    return Objects.hash(content, id, timestamp);
	}

	// Equals method
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    Message other = (Message) obj;
	    return Objects.equals(content, other.content) && Objects.equals(id, other.id)
	            && Objects.equals(timestamp, other.timestamp);
	}

	
	
}
