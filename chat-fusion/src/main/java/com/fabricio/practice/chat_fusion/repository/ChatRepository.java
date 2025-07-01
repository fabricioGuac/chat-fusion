package com.fabricio.practice.chat_fusion.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.fabricio.practice.chat_fusion.model.Chat;


//Repository interface for interacting with the "chats" collection in MongoDB
@Repository
public interface ChatRepository extends MongoRepository<Chat, String>{
	// Custom query method to find all chats where the user is a member
	@Query("{ 'members': ?0 }")
	public List<Chat> findChatByUserId(String userId);

	
	// Custom query method to find a single non-group chat with specific members
	@Query("{ $and: [ " +
	        "{ isGroup: false }, " +
	        "{ 'members.$id': { $all: [?0, ?1] } } ] }")
	Chat findSingleChatByUserIds(ObjectId id1, ObjectId id2);


	
}
