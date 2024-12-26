package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.util.List;


import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.MessageException;
import com.fabricio.practice.chat_fusion.model.Message;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.EditMessageRequest;
import com.fabricio.practice.chat_fusion.request.SendMessageRequest;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

//Service interface to define the message related business logic
public interface MessageService {
	 
	// Creates a message in the specified chat
	public Message sendMessage(SendMessageRequest req, User reqUser) throws  ChatException, MessageException , S3Exception, AwsServiceException, SdkClientException, IOException;
	
	// Retrieves messages from a specified chat with pagination 
	public List<Message> getChatMessages(String chatId, User reqUser, int limit, int skip) throws ChatException;
	
	// Finds a messaged by its unique ID
	public Message findMessageById(String messageId) throws MessageException; 
	
	// Updates the contents of an existing message
	public Message editMessage(EditMessageRequest req, String reqUserId) throws MessageException;
	
	// Deletes a message by its unique ID
	public void deleteMessage(String messageId , String reqUserId) throws MessageException, ChatException;
	
	
}
