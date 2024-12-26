package com.fabricio.practice.chat_fusion.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


//Implementation of the AwsService interface
@Service
public class AwsServiceImplementation implements AwsService{

	// AWS S3 client for interacting with Amazon S3
	private S3Client s3Client;

	// Name of the AWS S3 bucket used for storing files
	private String bucket;
	
	// Constructor for dependency injection of AWS S3 client and the bucket name
	public AwsServiceImplementation(S3Client s3Client, @Value("${aws.s3.bucket}") String bucket){
		this.s3Client = s3Client;
		this.bucket = bucket;
	}
	
	// Uploads a file to AWS S3
	@Override
	public String uploadFile(String key, InputStream file, String contentType) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		// Creates a request for uploading the file to the bucket
		PutObjectRequest request =  PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(contentType)
				.build();
		
		// Uploads the file to the AWS S3 bucket
		s3Client.putObject(request,RequestBody.fromInputStream(file, file.available()));
		
		// Returns the URL of the file in the  bucket
		return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toExternalForm();
	}

	// Deletes a single file from AWS S3 
	@Override
	public void deleteFile(String key) {
		
		// Creates a request to delete a file from the AWS S3 bucket
		DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		// Deletes the file from the AWS S3 bucket
		s3Client.deleteObject(request);
		
	}

	// Deletes all files in AWS S3 with a specific prefix
	@Override
	public void deleteAllChatFiles(String prefix) {

		// Request list of the of objects that match the prefix in the bucket
	    ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
	            .bucket(bucket)
	            .prefix(prefix)
	            .build();

	    // Gets the list of objects that match the prefix 
	    List<S3Object> objects = s3Client.listObjectsV2(listRequest).contents();

	    // Ensures the list of object is not empty before proceeding
	    if (!objects.isEmpty()) {
	    	
	        // Prepares a list of ObjectIdentifiers required for deletion
	        List<ObjectIdentifier> objectsToDelete = objects.stream()
	                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
	                .collect(Collectors.toList());

	        // Splits the list into smaller batches of 1000
	        List<List<ObjectIdentifier>> batches = partitionList(objectsToDelete, 1000);

	        // Loops through each batch and creates a delete request for that batch
	        for (List<ObjectIdentifier> batch : batches) {
	        	// Creates a delete request to delete the current batch from AWS S3
	            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
	                    .bucket(bucket)
	                    .delete(Delete.builder()
	                            .objects(batch) // Passes the batch of objects to be deleted
	                            .build())
	                    .build();
	            
	            // Deletes the file from the AWS S3 bucket
	            s3Client.deleteObjects(deleteRequest);
	        }
	    }
	}

	// Utility method to partition the list into smaller batches
	private List<List<ObjectIdentifier>> partitionList(List<ObjectIdentifier> list, int batchSize) {
		// Creates a list to hold smaller sublists (batches)
	    List<List<ObjectIdentifier>> partitions = new ArrayList<>();
	    // Loop through the original list and divide it into smaller sublists based on batchSiz
	    for (int i = 0; i < list.size(); i += batchSize) {
	    	// Creates a sublist starting from index 'i' and ending at 'i + batchSize' or the list's end
	        partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
	    }
	    return partitions;
	}
}
