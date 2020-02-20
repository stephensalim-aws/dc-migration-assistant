package com.atlassian.migration.datacenter.core.aws.auth;

public interface CredentialsStorer {

    void storeAccessKeyId(String accessKeyId);

    void storeSecretAccessKey(String secretAccessKey);

    void storeRegion(String region) throws InvalidAWSRegionException;
}
