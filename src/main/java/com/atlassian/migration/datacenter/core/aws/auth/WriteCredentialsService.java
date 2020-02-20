package com.atlassian.migration.datacenter.core.aws.auth;

public interface WriteCredentialsService {

    void storeAccessKeyId(String accessKeyId);

    void storeSecretAccessKey(String secretAccessKey);

}
