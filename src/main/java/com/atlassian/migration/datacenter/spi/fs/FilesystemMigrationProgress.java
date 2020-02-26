package com.atlassian.migration.datacenter.spi.fs;

public class FilesystemMigrationProgress {
    private FilesystemMigrationStatus status;

    private FailedFileMigrationReport report;

    public FilesystemMigrationProgress() {
        this(FilesystemMigrationStatus.NOT_STARTED);
    }

    public FilesystemMigrationProgress(FilesystemMigrationStatus status) {
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
