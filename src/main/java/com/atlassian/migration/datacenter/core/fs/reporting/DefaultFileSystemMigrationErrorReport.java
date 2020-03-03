package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages files which have had an error throughout the file migration
 * This class is threadsafe and is intended to be added to by any agents which
 * are a part of the file system migration
 */
public class DefaultFileSystemMigrationErrorReport implements FileSystemMigrationErrorReport {

    private final ConcurrentHashMap<FailedFileMigration, Void> failedMigrations;

    public DefaultFileSystemMigrationErrorReport() {
        this.failedMigrations = new ConcurrentHashMap<>();
    }

    /**
     * tries to report a failed file migration. If 100 files have failed already the report will be dropped to save memory.
     * In this scenario it is likely the migration will fail and that the lost errors will have the same cause.
     * @param failedFileMigration the failed file migration to report
     */
    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        if (failedMigrations.size() >= 100) {
            return;
        }
        failedMigrations.put(failedFileMigration, null);
    }

    /**
     * @return an immutable copy of the FailedFileMigrations in this report. Note the returned value
     * is not backed by the underlying collection so will not be updated as other producers add to it.
     */
    public Set<FailedFileMigration> getFailedFiles() {
        return ImmutableSet.copyOf(failedMigrations.keySet());
    }
}
