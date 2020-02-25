package com.atlassian.migration.datacenter.spi.fs;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FilesystemMigrationProgress {
    private FilesystemMigrationStatus status;
    private long bytesTransferred = 0L;
    private Set<Path> failedFiles;

    public FilesystemMigrationProgress() {
        this(FilesystemMigrationStatus.NOT_STARTED);
    }

    public FilesystemMigrationProgress(FilesystemMigrationStatus status) {
        this.status = status;
        this.failedFiles = new HashSet<>();
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
