package com.fabricio.practice.chat_fusion.service;

import java.util.List;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.request.GroupChatRequest;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

// Service interface to define the chat related business logic
public interface ChatService {

	// Creates a one to one chat between two users
	public Chat createChat(String reqUserId, String userId2) throws UserException;
	
	// Finds a chat based on its ID
	public Chat findChatById(String chatId) throws ChatException;
	
	// Retrieves all the chats a user is a member of
	public List<Chat> findAllChatsByUserId(String userId);
	
	// Creates a group chat with the specified detail 
	public Chat createGroup(GroupChatRequest req, String reqUserId) throws UserException;
	
	// Adds an user to a group chat
	public Chat addUserToGroup(String reqUserId, String userId2, String chatId) throws UserException, ChatException;
	
	// Grants admin privileges to an user in a group chat
	public Chat makeUserAdmin(String reqUserId, String userId2, String chatId) throws UserException, ChatException;
	
	// Updates a group chat details
	public Chat updateGroup(String reqUserId, String chatId, UpdateRequest req) throws ChatException, UserException;
	
	// Removes an user from a group chat
	public Chat removeFromGroup(String reqUserId, String userId2, String chatId) throws ChatException,UserException;
	
	// Deletes a chat
	public void deleteChat(String reqUserId, String chatId) throws ChatException,UserException;
	
}
