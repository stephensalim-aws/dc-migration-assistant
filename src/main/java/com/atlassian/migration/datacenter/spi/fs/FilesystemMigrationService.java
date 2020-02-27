package com.atlassian.migration.datacenter.spi.fs;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;

public interface FilesystemMigrationService {
    void startMigration();

    FileSystemMigrationReport getReport();

    boolean isRunning();
}
