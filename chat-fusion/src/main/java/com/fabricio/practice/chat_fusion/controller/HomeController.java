package com.fabricio.practice.chat_fusion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public ResponseEntity<String> HomeController() {
		return new ResponseEntity<String>("Hello from the HomeController", HttpStatus.OK);
		
	}
	
}
