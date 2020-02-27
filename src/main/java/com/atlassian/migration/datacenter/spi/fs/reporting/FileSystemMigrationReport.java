package com.atlassian.migration.datacenter.spi.fs.reporting;

import java.time.Duration;

public interface FileSystemMigrationReport extends FileSystemMigrationErrorReport, FileSystemMigrationProgress {

    void setStatus(FilesystemMigrationStatus status);

    FilesystemMigrationStatus getStatus();

    Duration getElapsedTime();

}
