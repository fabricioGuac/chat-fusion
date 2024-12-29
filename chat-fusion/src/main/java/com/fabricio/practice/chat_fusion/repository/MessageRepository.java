package com.fabricio.practice.chat_fusion.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.fabricio.practice.chat_fusion.model.Message;

public interface MessageRepository extends MongoRepository <Message, String>{

	// Custom query to find all messages from a chat
	@Query("{ 'chatId': ?0 }")
	public Page<Message> findByChatId(String chatId, Pageable pageable);
	
	// Method to delete all messages from a chat
	public void deleteAllByChatId(String chatId);



}
