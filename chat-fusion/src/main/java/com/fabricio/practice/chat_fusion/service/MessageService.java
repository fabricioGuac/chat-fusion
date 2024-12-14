package com.fabricio.practice.chat_fusion.service;

import java.util.List;


import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.MessageException;
import com.fabricio.practice.chat_fusion.model.Message;
import com.fabricio.practice.chat_fusion.request.EditMessageRequest;
import com.fabricio.practice.chat_fusion.request.SendMessageRequest;

//Service interface to define the message related business logic
public interface MessageService {
	 
	// Creates a message in the specified chat
	public Message sendMessage(SendMessageRequest req, String reqUserId) throws  ChatException, MessageException;
	
	// Retrieves messages from a specified chat with pagination 
	public List<Message> getChatMessages(String chatId, String reqUserId, int limit, int skip) throws ChatException;
	
	// Finds a messaged by its unique ID
	public Message findMessageById(String messageId) throws MessageException; 
	
	// Updates the contents of an existing message
	public Message editMessage(EditMessageRequest req, String reqUserId) throws MessageException;
	
	// Deletes a message by its unique ID
	public void deleteMessage(String messageId , String reqUserId) throws MessageException;
	
	// Deletes all messages from a chat
	public void deleteChatMessages(String chatId);
	
}
