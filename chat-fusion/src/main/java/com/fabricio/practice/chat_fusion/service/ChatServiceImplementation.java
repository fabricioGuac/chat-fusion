package com.fabricio.practice.chat_fusion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.repository.ChatRepository;
import com.fabricio.practice.chat_fusion.request.GroupChatRequest;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

//Implementation of the ChatService interface
@Service
public class ChatServiceImplementation implements ChatService {
	
	// Chat repository to interact with chat data in the database
	private ChatRepository chatRepository;
	// User Service for handling user-related operations
	private UserService userService;
	// Message Service for handling message-related operations
	private MessageService messageService;
	
	// Constructor for dependency injection of ChatRepository and UserService
	public ChatServiceImplementation(ChatRepository chatRepository, UserService userService, MessageService messageService) { 
		this.chatRepository = chatRepository;
		this.userService = userService;
		this.messageService = messageService;
	}
	

	// Creates a one to one chat between two users
	@Override
	public Chat createChat(String reqUserId, String userId2) throws UserException {
		
		userService.findUserById(userId2); // Validation to ensure the second user exists
		
		// Checks if a chat between the two users already exists
		Chat existingChat = chatRepository.findSingleChatByUserIds(reqUserId, userId2);
		if(existingChat != null ) {
			// Returns the existing chat if found
			return existingChat;
		}
		
		// Creates a new one to one chat
		Chat chat = new Chat();
		chat.setCreatedById(reqUserId);
		chat.getMemberIds().add(userId2);
		chat.getMemberIds().add(reqUserId);
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
	public Chat createGroup(GroupChatRequest req, String reqUserId) throws UserException {
		
		// Initializes a new group chat
		Chat groupChat = new Chat();
		groupChat.setGroup(true);
		groupChat.setChat_image(req.getChat_image());
		groupChat.setChat_name(req.getChat_name());
		groupChat.setCreatedById(reqUserId);
		groupChat.getAdminIds().add(reqUserId);
		
		// Validates that all provided user IDs correspond to existing users
		for (String memberId : req.getUserIds()) {
			userService.findUserById(memberId);
        }
		
		// Adds the requesting user and other members to the group chat
		groupChat.getMemberIds().add(reqUserId);
		groupChat.getMemberIds().addAll(req.getUserIds());

		// Saves the new group chat to the database and returns it
		return chatRepository.save(groupChat);
	}

	// Adds an user to a group chat
	@Override
	public Chat addUserToGroup(String reqUserId, String userId2, String chatId) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);
		userService.findUserById(userId2); //Validation to ensure the user exists

			if(chat.getAdminIds().contains(reqUserId)) {
				chat.getMemberIds().add(userId2);
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
	public Chat updateGroup(String reqUserId, String chatId, UpdateRequest req) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);
	        
	    	// Verifies the requesting user is a member of the chat
	        if(chat.getMemberIds().contains(reqUserId)) {
	            
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
	            if (req.getPfp() != null && !req.getPfp().isBlank()) {
	                chat.setChat_image(req.getPfp());
	            }

	            // Saves and return the updated chat
	            return chatRepository.save(chat);
	        }
	        
	     // Throws an exception if the user is not a member of the group
	        throw new ChatException("Non-members can't change the group details");
	    }



	// Removes an user from a group chat
	@Override
	public Chat removeFromGroup(String reqUserId, String userId2, String chatId) throws ChatException, UserException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);

	    userService.findUserById(userId2); // Validation to ensure the user exists
	    	
	    	// Ensures the operation is for a group chat
	        if (!chat.isGroup()) {
	            throw new ChatException("Operation only allowed for group chats");
	        }

	        // Verifies the user to be removed is a group member
	        if (!chat.getMemberIds().contains(userId2)) {
	            throw new ChatException("User is not a member of this group");
	        }

	        // Allows admins to remove any user
	        if (chat.getAdminIds().contains(reqUserId)) {
	        	chat.getMemberIds().remove(userId2);
	            chat.getAdminIds().remove(userId2);

	            // Handles the cases where there are no admins lefs
	            if (chat.getAdminIds().isEmpty()) {
	                if (!chat.getMemberIds().isEmpty()) {
	                    String newAdminId = chat.getMemberIds().iterator().next();
	                    chat.getAdminIds().add(newAdminId);
	                } else {
	                	// Deletes the group if no members remain
	                    deleteChat(reqUserId, chatId);
	                    return null;
	                }
	            }
	            // Save and return the updated chat
	            return chatRepository.save(chat);
	        }

	        // Allow users to remove themselves if they are not admins
	        if (chat.getMemberIds().contains(reqUserId) && reqUserId.equals(userId2)) {
	            chat.getMemberIds().remove(userId2);
	            return chatRepository.save(chat);
	        }

	        // Throws an exception if the requester lacks admin privileges
	        throw new ChatException("Non-admins can only remove themselves");
	    }


	

	// Deletes a chat
	@Override
	public void deleteChat(String reqUserId, String chatId) throws ChatException {
		// Retrieves the chat
		Chat chat = findChatById(chatId);

	        // Handles deletion for the groups
	        if (chat.isGroup()) {
	        	// Verifies the requesting user is an admin
	            if (chat.getAdminIds().contains(reqUserId)) {
	            	
	            	// Deletes the messages related to the chat
	                messageService.deleteChatMessages(chatId);
	            	
	                // Deletes the chat
	                chatRepository.deleteById(chatId);
	                return;
	            }
	            // Throws an exception if the requesting user is not an admin
	            throw new ChatException("Non-admin users cannot delete group chats");
	        }

	        // Handles deletion for one to one chats chats
	        if (chat.getMemberIds().contains(reqUserId)) {
	        	// Deletes the messages related to the chat
                messageService.deleteChatMessages(chatId);
            	
                // Deletes the chat
                chatRepository.deleteById(chatId);

	            return;
	        }

	        // Throws an exception if the user is not a member of the chat
	        throw new ChatException("You do not have permission to delete this chat");
	    }	
}
