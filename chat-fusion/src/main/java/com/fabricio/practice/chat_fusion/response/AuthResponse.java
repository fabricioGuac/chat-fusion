package com.fabricio.practice.chat_fusion.response;

//Response model for authentication operations
public class AuthResponse {

	// JWT token generated after successful authentication
	private String jwt;

	// A flag indicating if the authentication was successful
	private boolean isAuth;
	
	
	// Getter methods for the response fields
	public String getJwt() {
		return jwt;
	}

	public boolean isAuth() {
		return isAuth;
	}


	// Parameterized constructor to initialize the JWT and isAuth fields
	public AuthResponse(String jwt, boolean isAuth) {
		super();
		this.jwt = jwt;
		this.isAuth = isAuth;
	}
	
}
