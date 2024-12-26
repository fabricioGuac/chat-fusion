package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Service interface to define the AWS S3 related business logic
public interface AwsService {
	
	// Uploads a file to AWS S3
	public String uploadFile(String key, InputStream file, String contentType ) throws S3Exception, AwsServiceException, SdkClientException, IOException;
	
	// Deletes a single file from AWS S3 
	public void deleteFile(String key);
	
	// Deletes all files in AWS S3 with a specific prefix
	public void  deleteAllChatFiles(String prefix);
	
}
