package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;

import java.nio.file.Path;
import java.util.List;

public class DefaultFileSystemMigrationReport implements FileSystemMigrationReport {

    private final FileSystemMigrationErrorReport errorReport;
    private final FileSystemMigrationProgress progress;

    private FilesystemMigrationStatus status;

    public DefaultFileSystemMigrationReport(FileSystemMigrationErrorReport errorReport, FileSystemMigrationProgress progress) {
        this.errorReport = errorReport;
        this.progress = progress;
    }
    
    @Override
    public void setStatus(FilesystemMigrationStatus status) {
        this.status = status;
    }

    @Override
    public FilesystemMigrationStatus getStatus() {
        return status;
    }

    @Override
    public List<FailedFileMigration> getFailedFiles() {
        return errorReport.getFailedFiles();
    }

    @Override
    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        errorReport.reportFileNotMigrated(failedFileMigration);
    }

    @Override
    public List<Path> getMigratedFiles() {
        return progress.getMigratedFiles();
    }

    @Override
    public void reportFileMigrated(Path path) {
        progress.reportFileMigrated(path);
    }
}

