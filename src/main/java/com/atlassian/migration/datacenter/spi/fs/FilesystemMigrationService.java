package com.atlassian.migration.datacenter.spi.fs;

public interface FilesystemMigrationService {
    public void startMigration(FilesystemMigrationConfig config);

    public FilesystemMigrationProgress getProgress();
}
