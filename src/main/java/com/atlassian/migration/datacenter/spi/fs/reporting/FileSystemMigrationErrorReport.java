package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Represents the error status of a file system migration
 */
@JsonSerialize(as = FileSystemMigrationErrorReport.class)
public interface FileSystemMigrationErrorReport {

    List<FailedFileMigration> getFailedFiles();

    void reportFileNotMigrated(FailedFileMigration failedFileMigration);

}
