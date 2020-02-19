package com.atlassian.migration.datacenter.api.aws;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class AWSCredentialsWebObject {

    private String accessKeyId;
    private String secretAccessKey;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}
