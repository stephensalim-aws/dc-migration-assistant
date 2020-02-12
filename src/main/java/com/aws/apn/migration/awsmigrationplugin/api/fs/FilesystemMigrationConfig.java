package com.aws.apn.migration.awsmigrationplugin.api.fs;

import java.nio.file.Path;

public class FilesystemMigrationConfig {
    private final String s3Bucket;
    private final Path directoryToMigrate;

    public FilesystemMigrationConfig(String s3Bucket, Path directoryToMigrate) {
        this.s3Bucket = s3Bucket;
        this.directoryToMigrate = directoryToMigrate;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public Path getDirectoryToMigrate() {
        return directoryToMigrate;
    }
}
