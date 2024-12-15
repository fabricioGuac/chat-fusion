package com.fabricio.practice.chat_fusion.model;

import java.util.HashSet;
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
    private Set<String> adminIds = new HashSet<>();
 // Use @DBRef to reference User entities for members
    @DBRef
    private Set<User> members = new HashSet<>();

    // No-args constructor for serialization and deserialization frameworks
    public Chat() {
    }

    // Full-args constructor for initializing all fields of the Chat object
    public Chat(String id, String chat_name, String chat_image, boolean isGroup, String createdById,
                Set<String> adminIds, Set<User> members) {
        this.id = id;
        this.chat_name = chat_name;
        this.chat_image = chat_image;
        this.isGroup = isGroup;
        this.createdById = createdById;
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

    // hashCode, equals, and toString for proper object comparison and debugging
    @Override
    public int hashCode() {
        return Objects.hash(id, chat_name, createdById);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Chat other = (Chat) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Chat [id=" + id + ", chat_name=" + chat_name + ", isGroup=" + isGroup + "]";
    }
}