package com.fabricio.practice.chat_fusion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fabricio.practice.chat_fusion.exception.ChatException;
import com.fabricio.practice.chat_fusion.model.Chat;
import com.fabricio.practice.chat_fusion.model.Message;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.ChatRepository;
import com.fabricio.practice.chat_fusion.repository.MessageRepository;
import com.fabricio.practice.chat_fusion.request.SendMessageRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;


// Unit tests for MessageServiceImplementation
// Enables Mockito's integration with JUnit 5 (so @Mock and @InjectMocks work properly)
@ExtendWith(MockitoExtension.class)
public class MessageServiceTests {
	// Mock dependencies (we fake their behavior to test the MessageService logic)
    @Mock
    private ChatService chatService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private WebsocketService websocketService;
    @Mock
    private MessageRepository messageRepository;
    
    // Injects the above mocks into MessageServiceImplementation like Spring would
    // So when messageService calls a dependency it will use the mocks and not the real services
    @InjectMocks
    private MessageServiceImplementation messageService;

    // Unit test to ensure a text message is created and broadcasted correctly
    // Annotation to mark a method as a test
    @Test
    void sendMessage_shouldSendTextMessageSuccessfully() throws Exception {
        // Creates the mock user to send the message
        User sender = new User();
        sender.setId("64a7ff02b876123e9f6cfa91");
        
        // Creates the mock send message request
        SendMessageRequest request = new SendMessageRequest();
        request.setChatId("64a7ff02b876123e9f6cfa92");
        request.setType("text");
        request.setContent("Hello there");
        
        // Creates the mock chat to receive the message
        Chat chat = new Chat();
        chat.setId("64a7ff02b876123e9f6cfa92");
        chat.setMembers(new HashSet<>(List.of(sender)));
        chat.setConnectedUserIds(new HashSet<>());
        
        // Simulates that the chat exists when looked up by id
        when(chatService.findChatById("64a7ff02b876123e9f6cfa92")).thenReturn(chat);
        // Simulates saving to the DB by returning the same message object passed
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Executes the service method with the mocks in place
        Message result = messageService.sendMessage(request, sender);

        // Assertions, verifies the result
        assertNotNull(result); // The message is not null (exists)
        assertEquals("Hello there", result.getContent()); // The content is the passed one
        assertEquals("64a7ff02b876123e9f6cfa92", result.getChatId()); // The chat id is the passed one
        // Verifies side effects
        // Websocket should emmit to the chat id "chat123" a "send" event with a message
        verify(websocketService).messageEvent(eq("64a7ff02b876123e9f6cfa92"), eq("send"), any(Message.class));
        // Ensures the message was saved to the repository
        verify(messageRepository).save(any(Message.class));
    }
    
    // Unit test to no text message is created nor broadcasted when the id is invalid
    @Test
    void sendMessage_shoouldThrowExeptionForInvalidChatId() throws Exception {
    	// Creates the mock user to send the message
    	User sender = new User();
    	sender.setId("64a7ff02b876123e9f6cfa91");
    	
    	// Creates the mock send message request
        SendMessageRequest request = new SendMessageRequest();
        request.setChatId("64a7ff02b876123e9f6cfa92");
        request.setType("text");
        request.setContent("I won't work :(");
        
        // Simulates the chat lookup throws a ChatException
        when(chatService.findChatById("64a7ff02b876123e9f6cfa92")).thenThrow(new ChatException("Chat not found"));
        
        // Executes the service method with the mock in place and asserts it throws an exception
        ChatException ex = assertThrows(ChatException.class, () -> {
        	messageService.sendMessage(request, sender);
        });
        
        // Assertion, ensures the exception message matches
        assertEquals("Chat not found", ex.getMessage());
        // Verifies side effects
        // No websocket event should be emitted
        verify(websocketService, never()).messageEvent(any(), any(), any());
        // No message should be saved to the repository
        verify(messageRepository, never()).save(any());
    }
    
}

