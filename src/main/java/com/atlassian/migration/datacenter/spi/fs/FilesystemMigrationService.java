package com.atlassian.migration.datacenter.spi.fs;

public interface FilesystemMigrationService {
    void startMigration();

    DefaultFilesystemMigrationProgress getProgress();

    boolean isRunning();
}
