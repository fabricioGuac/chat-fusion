package com.fabricio.practice.chat_fusion.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.GroupChatRequest;
import com.fabricio.practice.chat_fusion.request.SingleChatRequest;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;
import com.fabricio.practice.chat_fusion.response.ApiResponse;
import com.fabricio.practice.chat_fusion.service.ChatService;
import com.fabricio.practice.chat_fusion.service.UserService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

//Controller responsible for chat-related API endpoints
@RestController
//Base path for all routes
@RequestMapping("/api/chats")
public class ChatController {
	
	// Service to handle chat-related business logic
	private ChatService chatService;
	// Service to handle user-related business logic
	private UserService userService;
	
	// Constructor to initialize the dependencies through dependency injection
	public ChatController (ChatService chatService, UserService userService) {
		this.chatService = chatService;
		this.userService = userService;
	}
	
	// Route to create a one to one chat
	@PostMapping("/single")
	public ResponseEntity<Chat>creatChatHandler(@RequestBody SingleChatRequest req, @RequestHeader("Authorization") String jwt) throws UserException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		// Creates the one to one chat with the provided IDs
		Chat chat = chatService.createChat(reqUser, req.getUserId());
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	// Route to create a group chat 
	@PostMapping(value = "/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Chat>creatGroupHandler(@ModelAttribute GroupChatRequest groupChatRequest, @RequestHeader("Authorization") String jwt) throws UserException, S3Exception, AwsServiceException, SdkClientException, IOException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		// Creates the group chat based on the request data and logged-in user's ID
		Chat groupChat = chatService.createGroup(groupChatRequest, reqUser);
		
		return  new ResponseEntity<Chat>(groupChat, HttpStatus.OK);
	}
	
	// Route to get a chat by it's ID
	@GetMapping("/{chatId}")
	public ResponseEntity<Chat>findChatByIdHandler(@PathVariable String chatId) throws ChatException {
		// Retrieves a chat based on it's ID
 		Chat chat = chatService.findChatById(chatId);
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	
	// Route to get all the chats an user is a member of
	@GetMapping("/user")
	public ResponseEntity<List<Chat>>findChatsByUserHandler( @RequestHeader("Authorization") String jwt) throws UserException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Retrieves a list of chats where the user is a member
		List<Chat> chats = chatService.findAllChatsByUserId(reqUser.getId());
		
		return  new ResponseEntity<List<Chat>>(chats, HttpStatus.OK);
	}
	
	
	// Route to add an user to a chat
	@PutMapping("/{chatId}/add/{userId}")
	public ResponseEntity<Chat>addUserToGroupHandler( @PathVariable String chatId, @PathVariable String userId,@RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		// Adds an user to an specified chat
		Chat chat = chatService.addUserToGroup(reqUser.getId(), userId, chatId);
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	// Route to grant admin privileges to an user 
	@PutMapping("/{chatId}/makeAdmin/{userId}")
	public ResponseEntity<Chat>makeUserAdminHandler( @PathVariable String chatId, @PathVariable String userId,@RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Grants an user admin privileges in an specified chat
		Chat chat = chatService.makeUserAdmin(reqUser.getId(), userId, chatId);
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	// Route to remove an user from a chat
	@PutMapping("/{chatId}/remove/{userId}")
	public ResponseEntity<Chat>removeUserFromGroupHandler( @PathVariable String chatId, @PathVariable String userId,@RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Removes an user from an specified chat
		Chat chat = chatService.removeFromGroup(reqUser, userId, chatId);
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	
	// Route to update a group chat 
	@PutMapping(value ="update/{chatId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Chat>renameGroupnHandler( @PathVariable String chatId, @ModelAttribute UpdateRequest req ,@RequestHeader("Authorization") String jwt) throws UserException, ChatException, S3Exception, AwsServiceException, SdkClientException, IOException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Updates the group chat image or name based on the request contents
		Chat chat = chatService.updateGroup(reqUser, chatId, req);
		
		return  new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	// Route to delete a chat
	@DeleteMapping("/delete/{chatId}")
	public ResponseEntity<ApiResponse>deleteChatHandler( @PathVariable String chatId, @RequestHeader("Authorization") String jwt) throws ChatException, UserException{
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Deletes the specified chat
		 chatService.deleteChat(reqUser, chatId);
		
		// Creates a response to indicate the chat was deleted successfully
		 ApiResponse res = new ApiResponse("Chat deleted successfully", true);
		 
		return  new ResponseEntity<ApiResponse>(res, HttpStatus.OK);
	}
	
}
