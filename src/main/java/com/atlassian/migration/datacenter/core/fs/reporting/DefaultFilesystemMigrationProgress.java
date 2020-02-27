package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;

import java.nio.file.Path;
import java.util.List;

public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {
    @Override
    public List<Path> getMigratedFiles() {
        return null;
    }

    @Override
    public void reportFileMigrated(Path path) {

    }
}
