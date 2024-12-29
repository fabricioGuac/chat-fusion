package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.ChatRepository;
import com.fabricio.practice.chat_fusion.repository.MessageRepository;
import com.fabricio.practice.chat_fusion.request.GroupChatRequest;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

//Implementation of the ChatService interface
@Service
public class ChatServiceImplementation implements ChatService {
	
	// Chat repository to interact with chat data in the database
	private ChatRepository chatRepository;
	// User Service for handling user-related operations
	private UserService userService;
	// Message repository to interact with message data in the database
	private MessageRepository messageRepository;
	// AWS Service to interact with the S3 bucket
	private AwsService awsS3Client;
	
	// Constructor for dependency injection of the necessary dependencies
	public ChatServiceImplementation(ChatRepository chatRepository, UserService userService, MessageRepository messageRepository, AwsService awsS3Client) { 
		this.chatRepository = chatRepository;
		this.userService = userService;
		this.messageRepository = messageRepository;
		this.awsS3Client = awsS3Client;
	}
	

	// Creates a one to one chat between two users
	@Override
	public Chat createChat(User reqUser, String userId2) throws UserException {
		
		User user2 = userService.findUserById(userId2); // Validation to ensure the second user exists
		
		// Checks if a chat between the two users already exists
		Chat existingChat = chatRepository.findSingleChatByUserIds(reqUser.getId(), userId2);
		if(existingChat != null ) {
			// Returns the existing chat if found
			return existingChat;
		}
		
		// Creates a new one to one chat
		Chat chat = new Chat();
		chat.setCreatedById(reqUser.getId());
		chat.getMembers().add(user2);
		chat.getMembers().add(reqUser);
		chat.setGroup(false);
		
		// Saves and returns the new chat
		return chatRepository.save(chat); 
		
	}

	// Finds a chat based on its ID
	@Override
	public Chat findChatById(String chatId) throws ChatException {
		// Queries the database for the chat
		Optional<Chat> optChat = chatRepository.findById(chatId);
		
		// Returns the chat if present 
		if(optChat.isPresent()) {
			return optChat.get();
		}
		// If the chat is not present throws a chat exception
		throw new ChatException("Chat not found by id " + chatId);
	}

	// Retrieves all the chats a user is a member of
	@Override
	public List<Chat> findAllChatsByUserId(String userId)  {
		// Fetches all chats where the user is a member
		List<Chat> chats = chatRepository.findChatByUserId(userId);
		return chats;
	}

	// Creates a group chat with the specified detail
	@Override
	public Chat createGroup(GroupChatRequest req, User reqUser) throws UserException, S3Exception, AwsServiceException, SdkClientException, IOException {
		
		// Initializes a new group chat
		Chat groupChat = new Chat();
		groupChat.setGroup(true);
		
		// Generates a unique ID for the group chat
	    String groupId = UUID.randomUUID().toString();
	    groupChat.setId(groupId);
		
		// Verifies if the request includes an image
		if(req.getChat_image() != null) {
			// Uploads the image to AWS S3 using the chat id and "pfp" as a prefix
			String awsUrl = awsS3Client.uploadFile(groupChat.getId()+"/pfp", req.getChat_image().getInputStream(), req.getChat_image().getContentType());
			// Sets the URL to the file as the chat image
			groupChat.setChat_image(awsUrl);
		}
		
		// Proceeds with chat initialization
		groupChat.setChat_name(req.getChat_name());
		groupChat.setCreatedById(reqUser.getId());
		groupChat.getAdminIds().add(reqUser.getId());
		
		// Adds the requesting user and other members to the group chat
		groupChat.getMembers().add(reqUser);
		
		// Validates that all provided user IDs correspond to existing users and adds them to the group
		for (String memberId : req.getUserIds()) {
			groupChat.getMembers().add(userService.findUserById(memberId));
		 }

		// Saves the new group chat to the database and returns it
		return chatRepository.save(groupChat);
	}

	// Adds an user to a group chat
	@Override
	public Chat addUserToGroup(String reqUserId, String userId2, String chatId) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);
		User user2 = userService.findUserById(userId2); //Validation to ensure the user exists

			if(chat.getAdminIds().contains(reqUserId)) {
				chat.getMembers().add(user2);
				// Saves and return the updated chat
				return chatRepository.save(chat);
				}
			
			// Throws an exception if the requester lacks admin privileges
			throw new ChatException("Non admins can't add users to the group");
		}
	
	
	
	// Grants admin privileges to an user in a group chat
	@Override 
	public Chat makeUserAdmin(String reqUserId, String userId2, String chatId) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);
	    
	    userService.findUserById(userId2); // Validation to ensure the user exists
	    
	        if (chat.getAdminIds().contains(reqUserId)) {
	        	// Adds the user as an admin if not already an admin
	            if (!chat.getAdminIds().contains(userId2)) {
	                chat.getAdminIds().add(userId2);
	                // Saves the group chat to the database and returns it
	                return chatRepository.save(chat);
	            }
	            throw new ChatException("User is already an admin");
	        }
			// Throws an exception if the requester lacks admin privileges
	        throw new ChatException("Non-admins can't give admin privileges");
	    }

	
	
	// Updates a group chat details
	@Override
	public Chat updateGroup( User reqUser, String chatId, UpdateRequest req) throws ChatException, UserException, S3Exception, AwsServiceException, SdkClientException, IOException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);
	        
	    	// Verifies the requesting user is a member of the chat
	        if(chat.getMembers().contains(reqUser)) {
	            
	            // Updates group name if provided and valid
	            if (req.getName() != null && !req.getName().isBlank()) {
	                String updatedGroupName = req.getName().trim();
	                
	                // Validates the name length
	                if (updatedGroupName.length() > 50) {
	                    throw new ChatException("Group name must not exceed 50 characters");
	                }
	                chat.setChat_name(updatedGroupName);
	            }

	            // Updates group image if provided
	            if (req.getPfp() != null) {
	            	// Overwrites the previous chat image in the AWS S3 bucket
	            	String awsUrl = awsS3Client.uploadFile(chatId+"/pfp", req.getPfp().getInputStream(), req.getPfp().getContentType());
	            	
	            	// If there was no previous image it sets the new image URL
	            	if(chat.getChat_image() == null) {
	            		chat.setChat_image(awsUrl);
	            	}
	            }

	            // Saves and return the updated chat
	            return chatRepository.save(chat);
	        }
	        
	     // Throws an exception if the user is not a member of the group
	        throw new ChatException("Non-members can't change the group details");
	    }



	// Removes an user from a group chat
	@Override
	public Chat removeFromGroup(User reqUser, String userId2, String chatId) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);

	    User user2 = userService.findUserById(userId2); // Validation to ensure the user exists
	    	
	    	// Ensures the operation is for a group chat
	        if (!chat.isGroup()) {
	            throw new ChatException("Operation only allowed for group chats");
	        }

	        // Verifies the user to be removed is a group member
	        if (!chat.getMembers().contains(user2)) {
	            throw new ChatException("User is not a member of this group");
	        }

	        // Allows admins to remove any user
	        if (chat.getAdminIds().contains(reqUser.getId())) {
	        	chat.getMembers().remove(user2);
	            chat.getAdminIds().remove(user2.getId());

	            // Handles the cases where there are no admins lefs
	            if (chat.getAdminIds().isEmpty()) {
	                if (!chat.getMembers().isEmpty()) {
	                    User newAdminId = chat.getMembers().iterator().next();
	                    chat.getAdminIds().add(newAdminId.getId());
	                } else {
	                	// Deletes the group if no members remain
	                    deleteChat(reqUser, chatId);
	                    return null;
	                }
	            }
	            // Save and return the updated chat
	            return chatRepository.save(chat);
	        }

	        // Allow users to remove themselves if they are not admins
	        if (chat.getMembers().contains(reqUser) && reqUser.getId().equals(userId2)) {
	            chat.getMembers().remove(user2);
	            return chatRepository.save(chat);
	        }

	        // Throws an exception if the requester lacks admin privileges
	        throw new ChatException("Non-admins can only remove themselves");
	    }


	

	// Deletes a chat
	@Override
	public void deleteChat(User reqUser, String chatId) throws ChatException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);

	        // Handles deletion for the groups
	        if (chat.isGroup()) {
	        	// Verifies the requesting user is an admin
	            if (chat.getAdminIds().contains(reqUser.getId())) {
	            	
	            	// Deletes the messages related to the chat
	            	messageRepository.deleteAllByChatId(chatId);
	            	
	            	// Deletes all files in the chat from the AWS S3 bucket
		        	awsS3Client.deleteAllChatFiles(chatId);
	            	
	                // Deletes the chat
	                chatRepository.deleteById(chatId);
	                return;
	            }
	            // Throws an exception if the requesting user is not an admin
	            throw new ChatException("Non-admin users cannot delete group chats");
	        }

	        // Handles deletion for one to one chats chats
	        if (chat.getMembers().contains(reqUser)) {
	        	// Deletes the messages related to the chat
	        	messageRepository.deleteAllByChatId(chatId);
	        	
	        	// Deletes all files in the chat from the AWS S3 bucket
	        	awsS3Client.deleteAllChatFiles(chatId);
            	
                // Deletes the chat
                chatRepository.deleteById(chatId);

	            return;
	        }

	        // Throws an exception if the user is not a member of the chat
	        throw new ChatException("You do not have permission to delete this chat");
	    }	
}
