package com.atlassian.migration.datacenter.spi.fs;

import com.google.common.collect.ImmutableList;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FailedFileMigrationReport {

    private final List<FailedFileMigration> failedMigrations;

    public FailedFileMigrationReport() {
        this.failedMigrations = new LinkedList<>();
    }

    public void reportFileNotMigrated(FailedFileMigration failedFileMigration) {
        failedMigrations.add(failedFileMigration);
    }

    public List<FailedFileMigration> getFailedFiles() {
        return ImmutableList.copyOf(failedMigrations);
    }

    public static class FailedFileMigration {
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
