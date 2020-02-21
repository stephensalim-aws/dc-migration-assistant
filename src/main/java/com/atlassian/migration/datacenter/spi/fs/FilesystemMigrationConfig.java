package com.atlassian.migration.datacenter.spi.fs;

public class FilesystemMigrationConfig {
    private String s3Bucket;
    private String directory;

    public FilesystemMigrationConfig() {
    }

    public FilesystemMigrationConfig(String s3Bucket, String directory) {
        this.s3Bucket = s3Bucket;
        this.directory = directory;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "FilesystemMigrationConfig{" +
                "s3Bucket='" + s3Bucket + '\'' +
                ", directory='" + directory + '\'' +
                '}';
    }
}
