package com.atlassian.migration.datacenter.core.fs;

public class S3Configuration {
    public static final long MAXIMUM_FILE_SIZE_TO_UPLOAD = 5 * 1024 * 1024 * 1024; // 5 GBs - https://docs.aws.amazon.com/AmazonS3/latest/dev/UploadingObjects.html
}
