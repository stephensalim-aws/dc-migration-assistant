package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.nio.file.Path;
import java.util.List;

/**
 * Tracks the progress of the file system migration
 */
@JsonSerialize(as = FileSystemMigrationProgress.class)
public interface FileSystemMigrationProgress {

    List<Path> getMigratedFiles();

    void reportFileMigrated(Path path);
}
