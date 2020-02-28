package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.atlassian.migration.datacenter.core.fs.reporting.DefaultFileSystemMigrationErrorReport;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents the error status of a file system migration
 */
public interface FileSystemMigrationErrorReport {

    List<FailedFileMigration> getFailedFiles();

    void reportFileNotMigrated(DefaultFileSystemMigrationErrorReport.FailedFileMigration failedFileMigration);

    class FailedFileMigration {
        private Path filePath;

        private String reason;

        public FailedFileMigration(Path filePath, String reason) {
            this.filePath = filePath;
            this.reason = reason;
        }

        public Path getFilePath() {
            return filePath;
        }

        public String getReason() {
            return reason;
        }
    }
}
