package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.NOT_STARTED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;

public class DefaultFileSystemMigrationReport implements FileSystemMigrationReport {

    private Clock clock;

    private final FileSystemMigrationErrorReport errorReport;
    private final FileSystemMigrationProgress progress;

    private Instant startTime;
    private Instant completeTime;
    private FilesystemMigrationStatus currentStatus;

    public DefaultFileSystemMigrationReport(FileSystemMigrationErrorReport errorReport, FileSystemMigrationProgress progress) {
        this.errorReport = errorReport;
        this.progress = progress;
        this.currentStatus = NOT_STARTED;
        this.clock = Clock.systemUTC();
    }

    @Override
    public void setStatus(FilesystemMigrationStatus status) {
        if (isStartingMigration(status)) {
            startTime = Instant.now(clock);
        } else if (isEndingMigration(status)) {
            completeTime = Instant.now(clock);
        }

        this.currentStatus = status;
    }

    @Override
    public FilesystemMigrationStatus getStatus() {
        return currentStatus;
    }

    @Override
    public Duration getElapsedTime() {
        Instant end = completeTime;
        if (isRunning()) {
            end = Instant.now(clock);
        }
        return Duration.between(startTime, end);
    }

    private boolean isRunning() {
        return currentStatus == RUNNING;
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

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    private boolean isStartingMigration(FilesystemMigrationStatus toStatus) {
        return this.currentStatus != RUNNING && toStatus == RUNNING;
    }

    private boolean isEndingMigration(FilesystemMigrationStatus toStatus) {
        return this.currentStatus == RUNNING && isTerminalState(toStatus);
    }

    private boolean isTerminalState(FilesystemMigrationStatus toStatus) {
        return toStatus == DONE || toStatus == FAILED;
    }
}

