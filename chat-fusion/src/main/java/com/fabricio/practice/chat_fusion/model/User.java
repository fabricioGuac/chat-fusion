package com.fabricio.practice.chat_fusion.model;



import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

// Marks this class as a MongoDB document corresponding to the "users" collection
@Document(collection = "users")
public class User {
	
	// Specifies the unique identifier for this document in the collection
	@Id
	private String id;
	private String username;
	// Creates a unique index on the email field in MongoDB ensuring fast lookups by email and enforcing uniqueness
	@Indexed(unique = true)
	private String email;
	private String password;
	private String pfp;
	
	// No-args constructor for serialization and deserialization frameworks
	public User() {
		
	}
	
	
	// Full-args constructor for initializing all fields of the User object 
	public User(String id, String username, String email, String password, String pfp) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.pfp = pfp;
	}



	// Getters and Setters for the fields
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPfp() {
		return pfp;
	}
	public void setPfp(String pfp) {
		this.pfp = pfp;
	}

	// To string method
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password + ", pfp="
				+ pfp + "]";
	}

	// Hash code method
	@Override
	public int hashCode() {
		return Objects.hash(email, id, password, pfp, username);
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
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(id, other.id)
				&& Objects.equals(password, other.password) && Objects.equals(pfp, other.pfp)
				&& Objects.equals(username, other.username);
	}
	
	
	
	
	
}
