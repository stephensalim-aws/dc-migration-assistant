package com.atlassian.migration.datacenter.spi.fs;

public interface FileSystemMigrationReport extends FileSystemMigrationErrorReport, FileSystemMigrationProgress {

    FilesystemMigrationStatus getStatus();

}
