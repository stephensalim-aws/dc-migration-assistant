package com.atlassian.migration.datacenter.core.fs;

import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.file.Path;

public class S3UploadConfig {
    private String bucketName;
    private S3AsyncClient s3AsyncClient;
    private Path sharedHome;

    public S3UploadConfig(String bucketName, S3AsyncClient s3AsyncClient, Path sharedHome) {
        this.bucketName = bucketName;
        this.s3AsyncClient = s3AsyncClient;
        this.sharedHome = sharedHome;
    }

    public String getBucketName() {
        return bucketName;
    }

    public S3AsyncClient getS3AsyncClient() {
        return s3AsyncClient;
    }

    public Path getSharedHome() {
        return sharedHome;
    }
}
