package com.atlassian.migration.datacenter.spi.fs;

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFilesystemMigrationProgress;

public interface FilesystemMigrationService {
    void startMigration();

    DefaultFilesystemMigrationProgress getProgress();

    boolean isRunning();
}
