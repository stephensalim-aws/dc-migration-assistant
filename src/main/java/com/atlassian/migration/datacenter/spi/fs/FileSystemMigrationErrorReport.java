package com.atlassian.migration.datacenter.spi.fs;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents the error status of a file system migration
 */
public interface FileSystemMigrationErrorReport {

    List<FailedFileMigration> getFailedFiles();

    void reportFileNotMigrated(FailedFileMigrationReport.FailedFileMigration failedFileMigration);

    class FailedFileMigration {
        private final Path filePath;

        private final String reason;

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
