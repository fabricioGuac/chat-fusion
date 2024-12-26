package com.fabricio.practice.chat_fusion.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Global exception handler that intercepts various exceptions and provides custom error responses
@RestControllerAdvice
public class GlobalException {

	// Handles UserException and provides a custom error response with status BAD_REQUEST
	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException ue, WebRequest req) {
		// Creates an ErrorDetail object with the user exception details
		ErrorDetail err = new ErrorDetail(ue.getMessage(), req.getDescription(false), LocalDateTime.now());
		
		return new ResponseEntity<ErrorDetail>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	// Handles MessageException and provides a custom error response with status BAD_REQUEST
	@ExceptionHandler(MessageException.class)
	public ResponseEntity<ErrorDetail> MessageExceptionHandler(MessageException me, WebRequest req) {
		// Creates an ErrorDetail object with the message exception details
		ErrorDetail err = new ErrorDetail(me.getMessage(), req.getDescription(false), LocalDateTime.now());
		
		return new ResponseEntity<ErrorDetail>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	
	// Handles ChatException and provides a custom error response with status BAD_REQUEST
	@ExceptionHandler(ChatException.class)
	public ResponseEntity<ErrorDetail> ChatExceptionHandler(ChatException ce, WebRequest req) {
		// Creates an ErrorDetail object with the chat exception details
		ErrorDetail err = new ErrorDetail(ce.getMessage(), req.getDescription(false), LocalDateTime.now());
			
		return new ResponseEntity<ErrorDetail>(err,HttpStatus.BAD_REQUEST);		
		}
	
	
	// Handles S3Exceptions and provides a custom error with a response status of INTERNAL_SERVER_ERROR
	@ExceptionHandler(S3Exception.class)
	public ResponseEntity<ErrorDetail> S3ExceptionHandler(S3Exception s3e, WebRequest req) {
		// Creates a custom error message with the message from the S3Exception
		String detailedMessage = "AWS S3 error: " + s3e.awsErrorDetails().errorMessage();
		// Creates an ErrorDetail object with the AWS S3 exception details
		ErrorDetail err = new ErrorDetail(detailedMessage, req.getDescription(false), LocalDateTime.now() );
		return new ResponseEntity<ErrorDetail>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// Handles generic AWS SDK client exceptions and provides a custom error with a response status of INTERNAL_SERVER_ERROR
	@ExceptionHandler(SdkClientException.class)
	public ResponseEntity<ErrorDetail> SdkClientExceptionHandler(SdkClientException sdkEx, WebRequest req) {
		// Creates an ErroDetail object with the AWS SDK client exception message
		ErrorDetail err = new ErrorDetail("AWS SDK client error: " + sdkEx.getMessage(), req.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
    // Handles MethodArgumentNotValidException, which occurs during validation failures
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetail> MethodArgumentNotValidExceptionHandler (MethodArgumentNotValidException e, WebRequest req) {
		// Retrieves the default validation error message for the first field error
		String error = e.getBindingResult().getFieldError().getDefaultMessage();
		// Creates an ErrorDetail object with a validation error details
		ErrorDetail err = new ErrorDetail("Validation Error", error, LocalDateTime.now());
		
		return new ResponseEntity<ErrorDetail>(err, HttpStatus.BAD_REQUEST);
	}
	
	// Handles NoHandlerFoundException, which occurs when no handler is found for a specific request
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorDetail> noHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest req ) {
		// Creates an ErrorDetail object indicating the endpoint was not found
		ErrorDetail error = new ErrorDetail("Endpoint not found", e.getMessage(), LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	
	
	// Handles any other exceptions that are not explicitly caught above
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetail> OtherExceptionHandler(Exception e, WebRequest req) {
		 // Creates an ErrorDetail object with the exception details
		ErrorDetail err = new ErrorDetail(e.getMessage(), req.getDescription(false), LocalDateTime.now());
		
		return new ResponseEntity<ErrorDetail>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	
	
}
