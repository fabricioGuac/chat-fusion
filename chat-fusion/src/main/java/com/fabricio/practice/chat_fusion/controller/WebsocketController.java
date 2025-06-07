package com.fabricio.practice.chat_fusion.controller;

import java.time.Instant;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fabricio.practice.chat_fusion.config.JwtProvider;
import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.CallEventRequest;
import com.fabricio.practice.chat_fusion.request.CallRequest;
import com.fabricio.practice.chat_fusion.request.UpdateStatusRequest;
import com.fabricio.practice.chat_fusion.service.ChatService;
import com.fabricio.practice.chat_fusion.service.UserService;

@Controller
public class WebsocketController {

	private SimpMessagingTemplate simpMessagingTemplate;
	private UserService userService; 
	private ChatService chatService;
	
	// Constructor for dependency injection
	public WebsocketController(SimpMessagingTemplate simpMessagingTemplate, JwtProvider  jwtProvider, UserService userService, ChatService chatService) {
	    this.simpMessagingTemplate = simpMessagingTemplate;
	    this.userService = userService;
	    this.chatService = chatService;
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
	
	// Endpoint to notify what users are connected to a chat
	@MessageMapping("/connected/{chatId}")
	public void handleUserChatConnection(@DestinationVariable String chatId, @Payload UpdateStatusRequest statusReq) throws ChatException {
		
		// If the user is connecting to the chat add it to the connectedUsers
		if(statusReq.isOnline()) {
			chatService.addConnectedUser(chatId, statusReq.getUserId());
			
		} else {
			// Removes the user from the connected list
			chatService.removeConnectedUser(chatId, statusReq.getUserId());
		}
		
		// Forwards the connection change to the chat to all users connected
		simpMessagingTemplate.convertAndSend("/chat/" + chatId + "/connected", statusReq);
	}
	
	// Endpoint to notify users of incoming calls
	@MessageMapping("/call/{chatId}")
	public void handleIncomingCall(@DestinationVariable String chatId, @Payload CallRequest callRequest) throws ChatException {
		// Gets the chat that is receiving a call event
		Chat chat = chatService.findChatById(chatId);
		
		// Notifies all members of the chat excluding the caller of the incoming call
		for(User member : chat.getMembers()) {
			if(!member.getId().equals(callRequest.getUserId())) {
				simpMessagingTemplate.convertAndSend("/topic/call/"+ member.getEmail(), callRequest);
			}
		}
	}
	
	// Endpoint to notify users of events in their current call
	@MessageMapping("/call-room/{chatId}")
public void handleCallRoomEvents(@DestinationVariable String chatId, @Payload CallEventRequest callEvent) {
		simpMessagingTemplate.convertAndSend("/topic/call-room/"+chatId, callEvent);
	}
}