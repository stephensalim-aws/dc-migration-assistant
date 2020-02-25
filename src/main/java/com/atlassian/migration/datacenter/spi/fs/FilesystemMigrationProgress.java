package com.atlassian.migration.datacenter.spi.fs;

public class FilesystemMigrationProgress {
    private FilesystemMigrationStatus status;
    private long bytesTransferred = 0L;

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

    public long getBytesTransferred() {
        return bytesTransferred;
    }

    public long addBytes(long bytesTransferred) {
        this.bytesTransferred += bytesTransferred;
        return this.bytesTransferred;
    }
}
