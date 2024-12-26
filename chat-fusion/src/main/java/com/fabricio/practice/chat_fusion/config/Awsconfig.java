package com.fabricio.practice.chat_fusion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// Configuration class for the AWS  S3 client
@Configuration
public class Awsconfig {
	// Access key for AWS services
	@Value("${aws.credential.accessKey}")
	private String accessKey;

	// Secret key for AWS services
	@Value("${aws.credential.secretKey}")
	private String secretKey;
	
	//  AWS region were the S3 bucked is located 
	@Value("${aws.region}")
	private String region;
	
	// Bean definition for theAWS S3 client
	// Creates and configures the  instance of the S3 client
	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(
							AwsBasicCredentials.create(accessKey, accessKey)
						))
				.build();
	}
}
