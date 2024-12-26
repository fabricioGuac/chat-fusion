package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.MessageException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.Message;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.MessageRepository;
import com.fabricio.practice.chat_fusion.request.EditMessageRequest;
import com.fabricio.practice.chat_fusion.request.SendMessageRequest;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Implementation of the MessageService interface
@Service
public class MessageServiceImplementation implements MessageService {


	// Chat Service for handling user-related operations
	public ChatService chatService;
	// Message repository to interact with message data in the database
	public MessageRepository messageRepository;
	// AWS Service to interact with the S3 bucket
	private AwsService awsS3Client;
	
	// Constructor for dependency injection of ChatService, MessageRepository and AwsService
	public MessageServiceImplementation(ChatService chatService, MessageRepository messageRepository, AwsService awsS3Client) {
		this.chatService = chatService;
		this.messageRepository = messageRepository;
		this.awsS3Client = awsS3Client;
	}
	
	// Creates a message in the specified chat
	@Override
	public Message sendMessage(SendMessageRequest req, User reqUser) throws ChatException, MessageException, S3Exception, AwsServiceException, SdkClientException, IOException {
		
		// Queries the chat in the database
		Chat chat = chatService.findChatById(req.getChatId());

		// Validates message content
		if ((req.getContent() == null || req.getContent().isBlank()) && req.getFile() == null) {
			throw new MessageException("Message content cannot be empty.");
		}
		
		// Creates a new message and sets it fields
		Message mssg = new Message();
		mssg.setUser(reqUser);
		
	    // Generates a unique ID for the message
	    String messageId = UUID.randomUUID().toString();
	    mssg.setId(messageId);
		
		// If a message is text, stores it to the database
		if(req.getType().equals("text")) {
		mssg.setContent(req.getContent().trim());
		} else {
			// If the message is a file, stores the file in AWS S3 and the URL in the database
			mssg.setContent(awsS3Client.uploadFile(chat.getId()+"/"+mssg.getId(), req.getFile().getInputStream(), req.getFile().getContentType()));
		}
		
		mssg.setType(req.getType());
		mssg.setChatId(chat.getId());
		mssg.setTimestamp(LocalDateTime.now());
		
		// Saves and returns the new message
		return messageRepository.save(mssg);
	}

	// Retrieves messages from a specified chat with pagination
	@Override
	public List<Message> getChatMessages(String chatId, User reqUser,int limit, int skip) throws ChatException{
		// Queries the chat in the database
	    Chat chat = chatService.findChatById(chatId);
	    
	    // Checks if the requesting user is a member of the chat
	    if (!chat.getMembers().contains(reqUser)) {
	        throw new ChatException("You are not a member of this chat and cannot view its messages.");
	    }
	    
	    // Queries messages with pagination, ordered by timestamp in descending order
	    Pageable pageable = PageRequest.of(skip / limit, limit, Sort.by(Sort.Order.desc("timestamp")));
	    Page<Message> messagePage = messageRepository.findByChatId(chatId, pageable);
	    

	    return messagePage.getContent();
	}

	

	// Finds a messaged by its unique ID
	@Override
	public Message findMessageById(String messageId) throws MessageException {
		// Searches for the message in the database
		Optional<Message> optMssg = messageRepository.findById(messageId);
		// If the message exists, returns it
		if(optMssg.isPresent()) {
			return optMssg.get();
		}
		// If the message is not found throws an exception
		throw new MessageException("Message not found by id: "+ messageId);
	}

	// Updates the contents of an existing message
	@Override
	public Message editMessage(EditMessageRequest req, String reqUserId) throws MessageException {
			// Fetches the message to be edited
			Message mssg = findMessageById(req.getMessageId());

	        // Ensures the requesting user is the author of the message
	        if (mssg.getUser().getId().equals(reqUserId)) {
	        	// Validates message new content
	    		if (req.getNewContent() == null || req.getNewContent().isBlank()) {
	    		    throw new MessageException("Message content cannot be empty.");
	    		}
	    		// Updates the message content and saves it
	            mssg.setContent(req.getNewContent());
	            return messageRepository.save(mssg);
	        }
	        // If the user is not the author of the message throws an exception
		 	throw new MessageException("Cannot update messages from another user");
		}

	// Deletes a message by its unique ID
	@Override
	public void deleteMessage(String messageId, String reqUserId) throws MessageException, ChatException {
		// Fetches the message by ID
		Message mssg = findMessageById(messageId);
		// Fetches the chat by ID
		Chat chat = chatService.findChatById(mssg.getChatId());

		
		// Booleans to check if the user is authorized to delete the message
		boolean isAuthor = mssg.getUser().getId().equals(reqUserId);
		boolean isAdmin = chat.isGroup() && chat.getAdminIds().contains(reqUserId);

	        if (isAuthor || isAdmin) {

	        	
	        	 if (!"text".equals(mssg.getType()) && mssg.getContent() != null && mssg.getContent().length() > 44) {
	                 try {
	                     // Extract the key from the message URL and delete the file
	                     String fileKey = mssg.getContent().substring(44);
	                     awsS3Client.deleteFile(fileKey);
	                 } catch (Exception e) {
	                     throw new MessageException("Failed to delete the associated file: " + e.getMessage());
	                 }
	             }
	        	
	        	// Deletes the message from the repository
	        	messageRepository.deleteById(messageId);
	        	
	            return;
	        } 
		 	// Throws an exception if the user is not the author of the message
		 	throw new MessageException("Cannot delete messages from another user");
		}

}
