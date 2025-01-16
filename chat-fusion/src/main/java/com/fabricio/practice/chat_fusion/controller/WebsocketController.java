package com.fabricio.practice.chat_fusion.controller;

import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fabricio.practice.chat_fusion.config.JwtProvider;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.request.UpdateStatusRequest;
import com.fabricio.practice.chat_fusion.service.UserService;

@Controller
public class WebsocketController {

	private SimpMessagingTemplate simpMessagingTemplate;
	private UserService userService; 
	
	// Constructor for dependency injection
	public WebsocketController(SimpMessagingTemplate simpMessagingTemplate, JwtProvider  jwtProvider, UserService userService) {
	    this.simpMessagingTemplate = simpMessagingTemplate;
	    this.userService = userService;
	}
	
	// Endpoint to notify what users are online
	@MessageMapping("/user.online.status")
	public void updateUserStatus(@Payload UpdateStatusRequest statusReq) throws UserException {
		// Gets the email and status from the request
		String email = statusReq.getEmail();
		boolean isOnline = statusReq.isOnline();
		

		// Updates the user's last connection
		userService.updateLastConnection(email, isOnline ? null : Instant.now());
		
		// Forwards the status change to all subscribers
		simpMessagingTemplate.convertAndSend("/topic/online-status/"+ email, isOnline);
	}
	 
}