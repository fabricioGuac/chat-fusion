package com.fabricio.practice.chat_fusion.service;

//Service interface to define the WebSocket related business logic
public interface WebsocketService {
	
	// Method to emit message related events (Send, edit or delete)
	public void messageEvent(String chatId, String type, Object payload);
	// Method to emit the chat related events
	public void chatNotificationEvent(String chatId, String userId, String type, Object payload);
}
