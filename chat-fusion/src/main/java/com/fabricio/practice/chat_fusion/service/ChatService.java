package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.util.List;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.GroupChatRequest;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Service interface to define the chat related business logic
public interface ChatService {

	// Creates a one to one chat between two users
	public Chat createChat(User reqUser, String userId2) throws UserException;
	
	// Finds a chat based on its ID
	public Chat findChatById(String chatId) throws ChatException;
	
	// Retrieves all the chats a user is a member of
	public List<Chat> findAllChatsByUserId(String userId);
	
	// Creates a group chat with the specified detail 
	public Chat createGroup(GroupChatRequest req, User reqUser) throws UserException, S3Exception, AwsServiceException, SdkClientException, IOException;
	
	// Adds an user to a group chat
	public Chat addUserToGroup(String reqUserId, String userId2, String chatId) throws UserException, ChatException;
	
	// Grants admin privileges to an user in a group chat
	public Chat makeUserAdmin(String reqUserId, String userId2, String chatId) throws UserException, ChatException;
	
	// Updates a group chat details
	public Chat updateGroup(User reqUser, String chatId, UpdateRequest req) throws ChatException, UserException, S3Exception, AwsServiceException, SdkClientException, IOException;
	
	// Removes an user from a group chat
	public Chat removeFromGroup(User reqUser, String userId2, String chatId) throws ChatException,UserException;
	
	// Deletes a chat
	public void deleteChat(User reqUser, String chatId) throws ChatException,UserException;
	
	// Adds a user to the connected users of a chat
	public void addConnectedUser(String chatId, String userId) throws ChatException;
	
	// Removes a user to the connected users of a chat
	public void removeConnectedUser(String chatId, String userId) throws ChatException;
}
