package com.fabricio.practice.chat_fusion.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    private String createdById;
    private Map<String, Integer> unreadCounts = new HashMap<>();
    private Set<String> adminIds = new HashSet<>();
    // Use @DBRef to reference User entities for members
    @DBRef
    private Set<User> members = new HashSet<>();

    // No-args constructor for serialization and deserialization frameworks
    public Chat() {
    }

    // Full-args constructor for initializing all fields of the Chat object
    public Chat(String id, String chat_name, String chat_image, boolean isGroup, String createdById,
			Map<String, Integer> unreadCounts, Set<String> adminIds, Set<User> members) {
		super();
		this.id = id;
		this.chat_name = chat_name;
		this.chat_image = chat_image;
		this.isGroup = isGroup;
		this.createdById = createdById;
		this.unreadCounts = unreadCounts;
		this.adminIds = adminIds;
		this.members = members;
	}


    // Getters and Setters
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

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public Set<String> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(Set<String> adminIds) {
        this.adminIds = adminIds;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
    
    

    public Map<String, Integer> getUnreadCounts() {
		return unreadCounts;
	}

	public void setUnreadCounts(Map<String, Integer> unreadCounts) {
		this.unreadCounts = unreadCounts;
	}

	// hashCode, equals, and toString for proper object comparison and debugging
	@Override
	public int hashCode() {
		return Objects.hash(adminIds, chat_image, chat_name, createdById, id, isGroup, members, unreadCounts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chat other = (Chat) obj;
		return Objects.equals(adminIds, other.adminIds) && Objects.equals(chat_image, other.chat_image)
				&& Objects.equals(chat_name, other.chat_name) && Objects.equals(createdById, other.createdById)
				&& Objects.equals(id, other.id) && isGroup == other.isGroup && Objects.equals(members, other.members)
				&& Objects.equals(unreadCounts, other.unreadCounts);
	}

	@Override
	public String toString() {
		return "Chat [id=" + id + ", chat_name=" + chat_name + ", chat_image=" + chat_image + ", isGroup=" + isGroup
				+ ", createdById=" + createdById + ", unreadCounts=" + unreadCounts + ", adminIds=" + adminIds
				+ ", members=" + members + "]";
	}

	
}