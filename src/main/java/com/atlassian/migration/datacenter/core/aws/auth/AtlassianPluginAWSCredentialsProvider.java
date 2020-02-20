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

    private final CredentialStorage credentialStorage;

    @Autowired
    public AtlassianPluginAWSCredentialsProvider(CredentialStorage credentialStorage) {
        this.credentialStorage = credentialStorage;
    }

    /**
     * AWS SDK V1 credentials API
     *
     * @return AWS Credentials to be used with SDK V1 clients
     */
    @Override
    public AWSCredentials getCredentials() {
        if (accessKeyIsDefined() && secretKeyIsDefined()) {
            return new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return credentialStorage.getAccessKeyId();
                }

                @Override
                public String getAWSSecretKey() {
                    return credentialStorage.getSecretAccessKey();
                }
            };
        }
        return new DefaultAWSCredentialsProviderChain().getCredentials();
    }

    private boolean secretKeyIsDefined() {
        return this.credentialStorage.getSecretAccessKey() != null && !this.credentialStorage.getSecretAccessKey().equals("");
    }

    private boolean accessKeyIsDefined() {
        return this.credentialStorage.getAccessKeyId() != null && !this.credentialStorage.getAccessKeyId().equals("");
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
     *
     * @return AWS Credentials to be used with SDK V2 clients
     */
    @Override
    public AwsCredentials resolveCredentials() {
        if (accessKeyIsDefined() && secretKeyIsDefined()) {
            return new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return credentialStorage.getAccessKeyId();
                }

                @Override
                public String secretAccessKey() {
                    return credentialStorage.getSecretAccessKey();
                }
            };
        }
        return DefaultCredentialsProvider.create().resolveCredentials();
    }
}
