package com.atlassian.migration.datacenter.spi.fs;

public interface FilesystemMigrationService {
    void startMigration();

    FilesystemMigrationProgress getProgress();

    boolean isRunning();
}
