package com.atlassian.migration.datacenter.spi.fs.reporting;

import java.time.Duration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = FileSystemMigrationReport.class)
public interface FileSystemMigrationReport extends FileSystemMigrationErrorReport, FileSystemMigrationProgress {

    void setStatus(FilesystemMigrationStatus status);

    FilesystemMigrationStatus getStatus();

    Duration getElapsedTime();

}
