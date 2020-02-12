package com.aws.apn.migration.awsmigrationplugin.core.fs;

import java.nio.file.Path;

public class FilesystemMigrationConfig {
    private final String s3Bucket;
    private final Path directoryToMigrate;

    public FilesystemMigrationConfig(String s3Bucket, Path directoryToMigrate) {
        this.s3Bucket = s3Bucket;
        this.directoryToMigrate = directoryToMigrate;
    }
}
