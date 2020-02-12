package com.aws.apn.migration.awsmigrationplugin.spi.fs;

public interface FilesystemMigrationService {
    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config);
}
