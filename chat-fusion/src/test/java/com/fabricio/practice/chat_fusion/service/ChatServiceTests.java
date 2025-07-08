package com.fabricio.practice.chat_fusion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.ChatRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Unit tests for ChatServiceImplementation
// Enables Mockito's integration with JUnit 5 (so @Mock and @InjectMocks work properly)
@ExtendWith(MockitoExtension.class)
public class ChatServiceTests {
	// Mock dependencies (we fake their behavior to test the chatService logic)
	@Mock 
	private UserService userService;
	@Mock 
	private ChatRepository chatRepository;
	@Mock
	private WebsocketService websocketService;
	
	// Injects the above mocks into ChatServiceImplementation like Spring would
	// So when chatService calls userService for example, it will use the mocks and no real services
	@InjectMocks
	private ChatServiceImplementation chatService;
	
	// Unit test to create a 1 to 1 chat when it does not exist already
	// Annotation to mark a method as a test
	@Test
	void createChat_shouldCreateOnetoOneChatIfNotExists() throws Exception {
		// Creates the mock users
		User reqUser= new User();
		reqUser.setId("64a7ff02b876123e9f6cfa91");
		
		User user2 = new User();
		user2.setId("64a7ff02b876123e9f6cfa92");
		
		// Simulates that user2 exists when looked up by id
		when(userService.findUserById("64a7ff02b876123e9f6cfa92")).thenReturn(user2);
		// Simulates there is no existing chat between the users
		when(chatRepository.findSingleChatByUserIds(any(), any())).thenReturn(null);
		// Simulates saving to the DB by returning the same Chat object passed in
		when(chatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		// Executes the service method with the mocks in place
		Chat result = chatService.createChat(reqUser, "64a7ff02b876123e9f6cfa92");
		
		// Assertions, verifies the result
		assertNotNull(result); // the chat is not null
		assertEquals(2, result.getMembers().size()); // Both users should be in the chat 
		// Verifies side effects
		// Websocket should notify both users
		verify(websocketService, times(2)).chatNotificationEvent(any(), any(), eq("addChat"), any());
		// Ensures the chat was saved to the repository
		verify(chatRepository).save(any(Chat.class));
	}
	
	// Unit test to ensure a new chat is not created when it already exists
	@Test
	void creatChat_shouldReturnExistingChatIfAlreadyExists() throws Exception{
		// Creates the mock users
		User reqUser = new User();
		reqUser.setId("64a7ff02b876123e9f6cfa91");
		User user2 = new User();
		user2.setId("64a7ff02b876123e9f6cfa92");
		// Creates the mock chat
		Chat existingChat = new Chat();
		existingChat.setId("69a7ff02b87f6cf46123ea92");
		
		//Simulates the user2 exists when looked up by id
		when(userService.findUserById("user2")).thenReturn(user2);
		// Simulates the chat exists when looked up 
		when(chatRepository.findSingleChatByUserIds(any(),any())).thenReturn(existingChat);
		
		// Executes the service method with the mocks in place
		Chat result = chatService.createChat(reqUser, "user2");
		
		// Assertion, verifies the result chat has the same id as the one we mocked 
		assertEquals("69a7ff02b87f6cf46123ea92", result.getId());
		// Verifies no chat is saved to the repository
		verify(chatRepository, never()).save(any());
		// Verifies no websocket event should be emitted
		verify(websocketService, never()).chatNotificationEvent(any(), any(), any(),any());
	}
}
