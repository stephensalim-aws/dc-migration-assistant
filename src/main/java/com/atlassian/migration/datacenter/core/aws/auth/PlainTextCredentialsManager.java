package com.atlassian.migration.datacenter.core.aws.auth;

public class PlainTextCredentialsManager implements CredentialsFetcher, CredentialsStorer {
    @Override
    public String getAccessKeyId() {
        return null;
    }

    @Override
    public String getSecretAccessKey() {
        return null;
    }

    @Override
    public void storeAccessKeyId(String accessKeyId) {
    }

    @Override
    public void storeSecretAccessKey(String secretAccessKey) {
    }
}
