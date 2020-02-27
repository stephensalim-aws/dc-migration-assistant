package com.atlassian.migration.datacenter.spi.fs;

public class DefaultFilesystemMigrationProgress {
    private FilesystemMigrationStatus status;

    private FailedFileMigrationReport report;

    public DefaultFilesystemMigrationProgress() {
        this(FilesystemMigrationStatus.NOT_STARTED);
    }

    public DefaultFilesystemMigrationProgress(FilesystemMigrationStatus status) {
        this.status = status;
    }

    public void setStatus(FilesystemMigrationStatus status) {
        this.status = status;
    }

    public void accumulateFileFailures(FailedFileMigrationReport newReport) {
        newReport.getFailedFiles()
                .parallelStream()
                .forEach(failure -> report.reportFileNotMigrated(failure));
    }

    public FilesystemMigrationStatus getStatus() {
        return status;
    }
}
