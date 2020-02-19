package com.atlassian.migration.datacenter.core.aws.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@Component
public class AtlassianPluginAWSCredentialsProvider implements AwsCredentialsProvider, AWSCredentialsProvider {

    CredentialsFetcher credentialsFetcher;

    @Autowired
    public AtlassianPluginAWSCredentialsProvider(CredentialsFetcher credentialsFetcher, CredentialsStorer credentialsStorer) {
        this.credentialsFetcher = credentialsFetcher;
    }

    /**
     * AWS SDK V1 credentials API
     * @return valid AWS credentials to be used to create AWS API clients
     */
    @Override
    public AWSCredentials getCredentials() {
        return new DefaultAWSCredentialsProviderChain().getCredentials();
    }

    /**
     * AWS SDK V1 credentials API
     * Refreshes the AWS credentials provided by this provider
     */
    @Override
    public void refresh() {
        new DefaultAWSCredentialsProviderChain().refresh();
    }

    /**
     * AWS SDK V2 credentials API
     * @return
     */
    @Override
    public AwsCredentials resolveCredentials() {
        return DefaultCredentialsProvider.create().resolveCredentials();
    }
}
