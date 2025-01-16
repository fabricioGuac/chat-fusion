package com.fabricio.practice.chat_fusion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Configuration class for WebSocket
@Configuration
// Enables WebSocket support with STOMP (Simple Text Oriented Messaging Protocol) messaging
@EnableWebSocketMessageBroker
public class WebsocketCofig implements WebSocketMessageBrokerConfigurer {
	
	// Registers WebSocket connection points for clients
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// Adds "/ws" endpoint for WebSocket connections, allows all origins and enables SockJS fallback
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
	}
	
	// Configures the message routing system for WebSocket communication
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// Prefix for client-to-server messages
		registry.setApplicationDestinationPrefixes("/app");
		// Enable in-memory message broker for chat destinations
	    registry.enableSimpleBroker("/chat", "/topic");
	}
	
}
