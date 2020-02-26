package com.atlassian.migration.datacenter.spi.fs;

public class FilesystemMigrationProgress {
    private FilesystemMigrationStatus status;

    public FilesystemMigrationProgress() {
        this(FilesystemMigrationStatus.NOT_STARTED);
    }

    public FilesystemMigrationProgress(FilesystemMigrationStatus status) {
        this.status = status;
    }

    public void setStatus(FilesystemMigrationStatus status) {
        this.status = status;
    }

    public FilesystemMigrationStatus getStatus() {
        return status;
    }
}
