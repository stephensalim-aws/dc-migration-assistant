package com.aws.apn.migration.awsmigrationplugin.api.fs;

import static com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationStatus.NOT_STARTED;

public class FilesystemMigrationProgress {
    private FilesystemMigrationStatus status;

    public FilesystemMigrationProgress() {
        this(NOT_STARTED);
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
