package io.spring;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.util.ValidationUtils;

public class AWSStaticCredentialsProvider implements AWSCredentialsProvider {

    private final AWSCredentials credentials;

    public AWSStaticCredentialsProvider(AWSCredentials credentials) {
        this.credentials = ValidationUtils.assertNotNull(credentials, "credentials");
    }

    public AWSCredentials getCredentials() {
        return credentials;
    }

    public void refresh() {
    }

}
