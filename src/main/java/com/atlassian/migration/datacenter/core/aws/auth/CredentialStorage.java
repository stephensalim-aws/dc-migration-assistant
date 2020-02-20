package com.atlassian.migration.datacenter.core.aws.auth;

public interface CredentialStorage {

    String AWS_CREDS_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.auth";
    String ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX = ".accessKeyId";
    String SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX = ".secretAccessKey";

    String getAccessKeyId();

    void setAccessKeyId(String accessKeyId);

    String getSecretAccessKey();

    void setSecretAccessKey(String secretAccessKey);

}
