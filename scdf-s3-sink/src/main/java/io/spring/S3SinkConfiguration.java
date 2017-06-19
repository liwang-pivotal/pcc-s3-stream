package io.spring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

@EnableConfigurationProperties(S3SinkProperties.class)
@EnableBinding(Sink.class)
public class S3SinkConfiguration {
	
	@Autowired
	S3SinkProperties config; 
	
	List<String> buffer = new ArrayList<>();
	
	@StreamListener(Sink.INPUT)
    public void log(Message<?> message) {
		
		File tempFile = null;
		
		if (buffer.size() >= config.getCount()) {
			try {
			    tempFile = File.createTempFile("temp", ".txt");

			    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
			    
			    for (String item : buffer) {
			    	out.write(item+"\n");
			    }
			    
			    out.close();			    
			    
			    upload(tempFile);
			    
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tempFile.delete();
			}
			
			buffer.clear();
		}
		
		buffer.add((String)message.getPayload());
		
    }
	
	private void upload(File file) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
		Date date = new Date();
		String bucketName     = config.getBucketName();
		String keyName        = dateFormat.format(date);
		
		AWSCredentials credentials = new BasicAWSCredentials(config.getAccessKey() , config.getSecretKey());
		
		AmazonS3ClientBuilder awsBuilder = AmazonS3ClientBuilder.standard();
		awsBuilder.setRegion(config.getBucketRegion());
		AmazonS3 s3client = awsBuilder.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		
		try {
            System.out.println("Uploading a new file to S3: " + file.getName());
            s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
            System.out.println("Successfully uploaded a new file to S3: " + file.getName());

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}

}
