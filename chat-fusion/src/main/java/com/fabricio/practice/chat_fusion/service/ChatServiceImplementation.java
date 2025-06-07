package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	// Websocket service for real time notifications
	private WebsocketService websocketService;
	
	// Constructor for dependency injection of the necessary dependencies
	public ChatServiceImplementation(ChatRepository chatRepository, UserService userService, MessageRepository messageRepository, AwsService awsS3Client, WebsocketService websocketService) { 
		this.chatRepository = chatRepository;
		this.userService = userService;
		this.messageRepository = messageRepository;
		this.awsS3Client = awsS3Client;
		this.websocketService = websocketService;
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
		chat.setId(UUID.randomUUID().toString()); 
		chat.setCreatedById(reqUser.getId());
		chat.getMembers().add(user2);
		chat.getMembers().add(reqUser);
		chat.setGroup(false);
		
		// Initializes unread counts
		chat.getUnreadCounts().put(reqUser.getId(), 0);
		chat.getUnreadCounts().put(userId2, 0);
		
		websocketService.chatNotificationEvent(chat.getId(), userId2, "addChat",chat);
		websocketService.chatNotificationEvent(chat.getId(), reqUser.getId(), "addChat",chat);
		
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
	    groupChat.setId(UUID.randomUUID().toString());
		
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
		// Initializes the unread count for the creator of the group
		groupChat.getUnreadCounts().put(reqUser.getId(), 0);
		
		// Handles null or empty userIds in case it is a solo group at the moment of creation
		List<String> userIds = req.getUserIds() != null ? req.getUserIds() : Collections.emptyList();
		
		// Validates that all provided user IDs correspond to existing users and adds them to the group
		for (String memberId : userIds) {
			groupChat.getMembers().add(userService.findUserById(memberId));
			// Initializes the unread counts
			groupChat.getUnreadCounts().put(memberId, 0);
		 }
		
		// Notifies all members of the creation of the group
		websocketService.chatNotificationEvent(groupChat.getId(), reqUser.getId(), "addChat", groupChat);
		for (String memberId : userIds) {
			websocketService.chatNotificationEvent(groupChat.getId(), memberId, "addChat", groupChat);
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
				// Initializes the unread counts for the new user
				chat.getUnreadCounts().put(userId2, 0);
				
				// Notify the users real time of the added member
				for (User member : chat.getMembers() ) {
					// For the new member add the chat to their list
					if(member.getId().equals(userId2)) {
						websocketService.chatNotificationEvent(chatId, userId2, "addChat", chat);
						continue;
					}
					// For old members add the user to their members list
					websocketService.chatNotificationEvent(chatId, member.getId(), "addMember", user2);
				 }
				
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
	                
	                // Notifies the user that is now an admin
	                websocketService.chatNotificationEvent(chatId, userId2, "addAdmin", chatId );
	                
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
	        	
	        	// Prepare the event payload
	            Map<String, Object> eventPayload = new HashMap<>();
	            eventPayload.put("chatId", chatId);
	            
	            // Updates group name if provided and valid
	            if (req.getName() != null && !req.getName().isBlank()) {
	                String updatedGroupName = req.getName().trim();
	                
	                // Validates the name length
	                if (updatedGroupName.length() > 50) {
	                    throw new ChatException("Group name must not exceed 50 characters");
	                }
	                
	                // Adds the name to the event payload
	                eventPayload.put("chat_name",updatedGroupName);
	                // Updates the chat name
	                chat.setChat_name(updatedGroupName);
	            }

	            // Updates group image if provided
	            if (req.getPfp() != null) {
	            	// Overwrites the previous chat image in the AWS S3 bucket
	            	String awsUrl = awsS3Client.uploadFile(chatId+"/pfp", req.getPfp().getInputStream(), req.getPfp().getContentType());
	            	
	            	// If there was no previous image it sets the new image URL
	            	if(chat.getChat_image() == null) {
	            		// Adds the name to the event payload
		                eventPayload.put("chat_image",awsUrl);
		                // Updates the chat image
	            		chat.setChat_image(awsUrl);
	            	}
	            }
	            
	            // Notifies all members of the changes
	            for( User member : chat.getMembers()) {	            	
	            	websocketService.chatNotificationEvent(chatId, member.getId(), "updateChat", eventPayload);
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
	            // Removes the user unread count
	            chat.getUnreadCounts().remove(userId2);
	            
	         // For the removed member remove the chat from their list
			 websocketService.chatNotificationEvent(chatId, userId2, "removeChat", chatId);

	            // Handles the cases where there are no admins lefs
	            if (chat.getAdminIds().isEmpty()) {
	                if (!chat.getMembers().isEmpty()) {
	                    User newAdminId = chat.getMembers().iterator().next();
	                    chat.getAdminIds().add(newAdminId.getId());
	                    // Notifies the user that is now an admin
		                websocketService.chatNotificationEvent(chatId, newAdminId.getId(), "addAdmin", chatId );
	                } else {
	                	// Deletes the group if no members remain
	                    deleteChat(reqUser, chatId);
	                    return null;
	                }
	            }
	            
	            
	            // Notifies the users real time of the removed member
				for (User member : chat.getMembers() ) {
					// For other members removes the user from their members list
					websocketService.chatNotificationEvent(chatId, member.getId(), "removeMember", userId2);
				 }
	            
	            // Save and return the updated chat
	            return chatRepository.save(chat);
	        }

	        // Allow users to remove themselves if they are not admins
	        if (chat.getMembers().contains(reqUser) && reqUser.getId().equals(userId2)) {
	            chat.getMembers().remove(user2);
	            // Removes the user unread count
	            chat.getUnreadCounts().remove(userId2);
	            
	            // Notifies the users real time of the removed member
				for (User member : chat.getMembers() ) {
					// For the removed member remove the chat from their list
					if(member.getId().equals(userId2)) {
						websocketService.chatNotificationEvent(chatId, userId2, "removedChat", chat.getId());
						continue;
					}
					// For other members removes the user from their members list
					websocketService.chatNotificationEvent(chatId, member.getId(), "removeMember", chat.getId());
				 }
				
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
	            	
	            	// Notifies all members that the chat will be deleted
	                for (User member : chat.getMembers()) {
	                    websocketService.chatNotificationEvent(chatId, member.getId(), "removeChat", chatId);
	                }
	            	
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
	        	
	        	// Notifies both users that the chat will be deleted
	            for (User member : chat.getMembers()) {
	                websocketService.chatNotificationEvent(chatId, member.getId(), "removeChat", chatId);
	            }
	        	
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


	// Adds a user to the connected users of a chat
	@Override
	public void addConnectedUser(String chatId, String userId) throws ChatException {
		Chat chat = findChatById(chatId);
		chat.getConnectedUserIds().add(userId);
		chatRepository.save(chat);
		return;
		
	}

	// Removes a user to the connected users of a chat
	@Override
	public void removeConnectedUser(String chatId, String userId) throws ChatException {
		Chat chat = findChatById(chatId);
		chat.getConnectedUserIds().remove(userId);
		chatRepository.save(chat);
		return;
		
	}	
}
