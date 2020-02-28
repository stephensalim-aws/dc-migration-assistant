package com.atlassian.migration.datacenter.spi.fs.reporting;

import java.util.List;

/**
 * Represents the error status of a file system migration
 */
public interface FileSystemMigrationErrorReport {

    List<FailedFileMigration> getFailedFiles();

    void reportFileNotMigrated(FailedFileMigration failedFileMigration);

}
