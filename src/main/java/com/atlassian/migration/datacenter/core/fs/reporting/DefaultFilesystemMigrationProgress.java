package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@JsonAutoDetect
public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {

    private List<Path> migratedFiles = new LinkedList<>();

    @Override
    public List<Path> getMigratedFiles() {
        return migratedFiles;
    }

    @Override
    public void reportFileMigrated(Path path) {
        migratedFiles.add(path);
    }
}
