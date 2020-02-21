package com.atlassian.migration.datacenter.core.aws.auth;

public interface ReadCredentialsService {

    String getAccessKeyId();

    String getSecretAccessKey();

}
