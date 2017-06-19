package io.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("s3")
public class S3SinkProperties {
	
	private int count = 100;
	
	private String accessKey;
	private String secretKey;
	private String bucketRegion;
	private String bucketName;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getBucketRegion() {
		return bucketRegion;
	}
	public void setBucketRegion(String bucketRegion) {
		this.bucketRegion = bucketRegion;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
}