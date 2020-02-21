package com.atlassian.migration.datacenter.spi.fs;

public interface FilesystemMigrationService {
    void startMigration(FilesystemMigrationConfig config);

    FilesystemMigrationProgress getProgress();

    boolean isRunning();
}
