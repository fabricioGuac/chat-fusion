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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.exception.MessageException;
import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.Message;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.request.EditMessageRequest;
import com.fabricio.practice.chat_fusion.request.SendMessageRequest;
import com.fabricio.practice.chat_fusion.response.ApiResponse;
import com.fabricio.practice.chat_fusion.service.MessageService;
import com.fabricio.practice.chat_fusion.service.UserService;
import com.fabricio.practice.chat_fusion.service.WebsocketService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	// Service to handle message-related business logic
	private MessageService messageService;
	// Service to handle user-related business logic
	private UserService userService; 
	
	// Constructor to initialize the dependencies through dependency injection
	public MessageController(MessageService messageService, UserService userService, WebsocketService websocketService ) {
		this.messageService = messageService;
		this.userService = userService;
	}
	
	// Route to create a new message
	@PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Message> sendMessageHandler(@ModelAttribute SendMessageRequest req, @RequestHeader("Authorization") String jwt) throws  ChatException, MessageException, UserException, S3Exception, AwsServiceException, SdkClientException, IOException {
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		// Creates the message
		Message mssg = messageService.sendMessage(req, reqUser);

		return new ResponseEntity<Message>(mssg, HttpStatus.OK);
	}

	
	// Route to get messages from a chat with pagination
	@GetMapping("/chat/{chatId}")
	public ResponseEntity<List<Message>> getChatMessageHandler(
	    @PathVariable String chatId, 
	    @RequestHeader("Authorization") String jwt,
	    @RequestParam(defaultValue = "0") int skip)    // Default to no skipped messages
	    throws ChatException, UserException {

	    // Retrieves the user's profile based on the JWT token
	    User reqUser = userService.findUserProfile(jwt);

	    // Retrieves paginated messages for the specified chat
	    List<Message> mssgs = messageService.getChatMessages(chatId, reqUser, 100, skip);

	    // Returns the paginated list of messages with an HTTP OK status
	    return new ResponseEntity<>(mssgs, HttpStatus.OK);
	}

	
	// Route to edit the content of an existing message
	@PutMapping("/edit")
	public ResponseEntity <Message> editMessageHandler(@RequestBody EditMessageRequest req, @RequestHeader("Authorization") String jwt) throws  MessageException, UserException {
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		// Calls the service to update the message content
		Message updatedMssg = messageService.editMessage(req, reqUser.getId());
		
		return new ResponseEntity<Message>(updatedMssg, HttpStatus.OK);
	}
	
	// Route to delete a message
	@DeleteMapping("/delete/{messageId}/{chatId}")
	public ResponseEntity <ApiResponse> deleteMessageHandler(@PathVariable String messageId, @PathVariable String chatId, @RequestHeader("Authorization") String jwt) throws  MessageException, UserException, ChatException {
		// Retrieves the user's profile based on the JWT token
		User reqUser = userService.findUserProfile(jwt);
		
		// Calls the service to delete the message if the user is authorized
		messageService.deleteMessage(messageId, reqUser.getId());
		
		// Creates a response object to indicate successful deletion
		ApiResponse res = new ApiResponse("Message deleted successfully", true);
		
		return new ResponseEntity<ApiResponse>(res, HttpStatus.OK);
	}
	
	
}
