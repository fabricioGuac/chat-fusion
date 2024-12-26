package com.fabricio.practice.chat_fusion.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

//Marks this class as a MongoDB document corresponding to the "messages" collection
@Document(collection = "messages")
public class Message {

    // Specifies the unique identifier for this document in the collection
    @Id
    private String id;
    private String type;
    private String content;
    // Indexes the time stamp to improve query performance
    @Indexed
    private LocalDateTime timestamp;

    // Use @DBRef to reference User entities for user
    @DBRef
    private User user;
    private String chatId; 

    // No-args constructor for serialization and deserialization frameworks
    public Message() {
    }

    // Full-args constructor for initializing all fields of the Message object
    public Message(String id, String type, String content, LocalDateTime timestamp, User user, String chatId) {
		super();
		this.id = id;
		this.type = type;
		this.content = content;
		this.timestamp = timestamp;
		this.user = user;
		this.chatId = chatId;
	}

	// Getters and Setters
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    // To string method
	@Override
	public String toString() {
		return "Message [id=" + id + ", type=" + type + ", content=" + content + ", timestamp=" + timestamp
				+ ", user=" + user + ", chatId=" + chatId + "]";
	}

	// Hash code method
	@Override
	public int hashCode() {
		return Objects.hash(chatId, content, id, timestamp, type, user);
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
		return Objects.equals(chatId, other.chatId) && Objects.equals(content, other.content)
				&& Objects.equals(id, other.id) && Objects.equals(timestamp, other.timestamp)
				&& Objects.equals(type, other.type) && Objects.equals(user, other.user);
	}
}
