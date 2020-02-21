package com.atlassian.migration.datacenter.core.aws.auth;

public interface CredentialsManager {

    String AWS_CREDS_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.auth";
    String ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX = ".accessKeyId";
    String SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX = ".secretAccessKey";

}
