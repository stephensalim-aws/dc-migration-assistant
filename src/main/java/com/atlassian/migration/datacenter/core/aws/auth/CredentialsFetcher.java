package com.atlassian.migration.datacenter.core.aws.auth;

public interface CredentialsFetcher {

    String getAccessKeyId();

    String getSecretAccessKey();

    String getRegion();

}
