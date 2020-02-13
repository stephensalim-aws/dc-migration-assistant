package com.aws.apn.migration.awsmigrationplugin.spi.fs;

public interface FilesystemMigrationService {
    public void startMigration(FilesystemMigrationConfig config);

    public FilesystemMigrationProgress getProgress();
}
