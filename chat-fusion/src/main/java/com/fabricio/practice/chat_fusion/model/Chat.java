package com.fabricio.practice.chat_fusion.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//Marks this class as a MongoDB document corresponding to the "chats" collection
@Document(collection = "chats")
public class Chat {

		// Specifies the unique identifier for this document in the collection
		@Id
		private String id;
		private String chat_name;
		private String chat_image;
		private boolean isGroup;
		private User createdBy;
		private Set<User> members = new HashSet<>();
		private List<Message> messages = new ArrayList<>();
		
		// No-args constructor for serialization and deserialization frameworks
		public Chat () {
			
		}
		// Full-args constructor for initializing all fields of the Chat object
		public Chat(String id, String chat_name, String chat_image, boolean isGroup, User createdBy, Set<User> members,
				List<Message> messages) {
			super();
			this.id = id;
			this.chat_name = chat_name;
			this.chat_image = chat_image;
			this.isGroup = isGroup;
			this.createdBy = createdBy;
			this.members = members;
			this.messages = messages;
		}

		// Getters and Setters for the fields
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		public boolean isGroup() {
			return isGroup;
		}

		public void setGroup(boolean isGroup) {
			this.isGroup = isGroup;
		}

		public User getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(User createdBy) {
			this.createdBy = createdBy;
		}

		public Set<User> getMembers() {
			return members;
		}

		public void setMembers(Set<User> members) {
			this.members = members;
		}

		public List<Message> getMessages() {
			return messages;
		}

		public void setMessages(List<Message> messages) {
			this.messages = messages;
		}
		
		// To string method
		@Override
		public String toString() {
		    return "Chat [id=" + id + ", chat_name=" + chat_name + ", chat_image=" + chat_image + ", isGroup=" + isGroup
		            + ", createdBy=" + createdBy + "]";
		}

		// Hash code method
		@Override
		public int hashCode() {
		    return Objects.hash(chat_image, chat_name, createdBy, id, isGroup);
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
		    Chat other = (Chat) obj;
		    return Objects.equals(chat_image, other.chat_image) && Objects.equals(chat_name, other.chat_name)
		            && Objects.equals(createdBy, other.createdBy) && Objects.equals(id, other.id)
		            && isGroup == other.isGroup;
		}

		
		
	
}
