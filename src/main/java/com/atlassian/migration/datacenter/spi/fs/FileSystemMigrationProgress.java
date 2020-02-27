package com.atlassian.migration.datacenter.spi.fs;

import java.nio.file.Path;
import java.util.List;

/**
 * Tracks the progress of the file system migration
 */
public interface FileSystemMigrationProgress {

    List<Path> getMigratedFiles();

    void reportFileMigrated(Path path);
}
