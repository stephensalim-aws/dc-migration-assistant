package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages files which have had an error throughout the file migration
 * This class is threadsafe and is intended to be added to by any agents which
 * are a part of the file system migration
 */
@JsonAutoDetect
public class DefaultFileSystemMigrationErrorReport implements FileSystemMigrationErrorReport {

    private final ConcurrentLinkedQueue<FailedFileMigration> failedMigrations;

    public DefaultFileSystemMigrationErrorReport() {
        this.failedMigrations = new ConcurrentLinkedQueue<>();
    }

    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        failedMigrations.add(failedFileMigration);
    }

    /**
     * @return an immutable copy of the FailedFileMigrations in this report. Note the returned value
     * is not backed by the underlying collection so will not be updated as other producers add to it.
     */
    public List<FailedFileMigration> getFailedFiles() {
        return ImmutableList.copyOf(failedMigrations);
    }
}
