package com.atlassian.migration.datacenter.core.aws.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

public class AtlassianPluginAWSCredentialsProvider implements AwsCredentialsProvider, AWSCredentialsProvider {

    CredentialsFetcher credentialsFetcher;

    public AtlassianPluginAWSCredentialsProvider(CredentialsFetcher credentialsFetcher) {
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
