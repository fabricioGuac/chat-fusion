package com.fabricio.practice.chat_fusion.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

//Implementation of the Websocket interface
@Service
public class WebsocketServiceImplementation implements WebsocketService{

	// Template for sending messages to clients over WebSocket
	private SimpMessagingTemplate simpMessagingTemplate;
	
	  // Constructor for dependency injection
    public WebsocketServiceImplementation(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
	
	// Method to emit message related events (Send, edit or delete)
	@Override
	public void messageEvent(String chatId, String type, Object payload) {
		 Map<String, Object> event = new HashMap<>();
		 // Sets the message event type
	     event.put("type", type);
	     // Sets the event payload
	     event.put("payload", payload);
	     // Emits WebSocket message event
	     simpMessagingTemplate.convertAndSend("/chat/" + chatId, event);
	}
	
	// Method to emit the chat related events
	@Override
	public void chatNotificationEvent(String chatId, String userId, String type, Object payload) {
		Map<String, Object> event = new HashMap<>();
		
		event.put("type", type);
		
		event.put("chatId", chatId);
		
		event.put("payload", payload);
		
		 // Emits WebSocket message event
		 simpMessagingTemplate.convertAndSend("/chat/notifications/" + userId, event);
		}

}
