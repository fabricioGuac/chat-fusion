package com.fabricio.practice.chat_fusion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fabricio.practice.chat_fusion.exception.UserException;
import com.fabricio.practice.chat_fusion.model.User;
import com.fabricio.practice.chat_fusion.repository.UserRepository;
import com.fabricio.practice.chat_fusion.request.UpdateRequest;

// Unit tests for UserServiceImplementation
//Enables Mockito's integration with JUnit 5 (so @Mock and @InjectMocks work properly)
@ExtendWith(MockitoExtension.class)
public class UserSeviceTests {
	// Mock dependencies (we fake their behavior to test the MessageService logic)
	@Mock
	private UserRepository userRepository;
	@Mock 
	private AwsService awsS3Client;
	// Injects the above mocks into UserServiceImplementation like Spring would
    // So when userService calls a dependency it will use the mocks and not the real services
	@InjectMocks
	private UserServiceImplementation userService;
	
	// Unit test to ensure the matching users are found
	// Annotation to mark a method as a test
	@Test
	void searchUsers_shouldReturnMatchingUsers() {
		// Query to search for users
		String query = "jhon";
		
		// Creates the mock users
		User user1 = new User();
		user1.setId("64a7ff02b876123e9f6cfa91");
		user1.setUsername("johnny");
		user1.setEmail("jhonny@gmail.com");
		
		User user2 = new User();
		user2.setId("64a7ff02b876123e9f6cfa92");
		user2.setUsername("jhonathan");
		user2.setEmail("jhonathan2gmail.com");
		
		// Makes a list from the mock users
		List<User> mockResults = List.of(user1,user2);
		// Simulates the query returns the mock users
		when(userRepository.searchUser(query)).thenReturn(mockResults);
		
		// Executes the service method with the mocks in place
		List<User> result = userService.searchUsers(query);
		
		// Assertions
		assertNotNull(result); // The result is not null (exists)
		assertEquals(2,result.size()); // The list should contain 2 user objects
		assertEquals("johnny", result.get(0).getUsername()); // Verifies that the method returns the mock list in the order it was provided
		// Verifies the query is executed
		verify(userRepository).searchUser(query);
	}
	
	// Unit test to ensure updateUser throws an exception when the username exceeds 50 characters
	@Test
	void updateUser_shouldThrowExceptionIfUsernameTooLong() throws Exception {
		// Mock user id
		String userId = "64a7ff02b876123e9f6cfa91";
		// Creates the mock user with that id
		User user1 = new User();
		user1.setId(userId);
		// Creates the mock update request
		UpdateRequest req = new UpdateRequest();
		// Sets the request name to 'a' 51 times (too long)
		req.setName("a".repeat(51));
		
		// Simulates the user is found when looked up by id
		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
		
		// Executes the service method with the mock in place and asserts it throws an exception
		UserException ex = assertThrows(UserException.class, () -> {
			userService.updateUser(userId, req);
		});
		
		// Assertion, ensures the exception message matches
		assertEquals("Username must not exceed 50 characters", ex.getMessage());
		// Verifies side effects
		// Should not attempt to upload anything to S3
		verify(awsS3Client, never()).uploadFile(any(), any(), any());
		// Should not attempt to save to the database
		verify(userRepository, never()).save(any());
	}
}
