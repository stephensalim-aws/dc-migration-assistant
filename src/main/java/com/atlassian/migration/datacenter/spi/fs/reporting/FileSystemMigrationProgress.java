package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.nio.file.Path;
import java.util.List;

/**
 * Tracks the progress of the file system migration
 */
@JsonSerialize(as = FileSystemMigrationProgress.class)
public interface FileSystemMigrationProgress {

    /**
     * Gets the number of files which have been found to migrate.
     */
    @JsonProperty("filesFound")
    Long getNumberOfFilesFound();

    void reportFileFound();

    /**
     * Gets the number of files which are currently being uploaded but not yet confirmed to be uploaded successfully
     */
    @JsonProperty("filesInFlight")
    Long getNumberOfFilesInFlight();

    void reportFileInFlight();

    List<Path> getMigratedFiles();

    void reportFileMigrated(Path path);
}
