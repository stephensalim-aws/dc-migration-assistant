package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {

    private List<Path> migratedFiles = new LinkedList<>();

    private AtomicLong filesFound = new AtomicLong(0);

    @Override
    public Long getNumberOfFilesFound() {
        return filesFound.get();
    }

    @Override
    public void reportFileFound() {
        filesFound.incrementAndGet();
    }

    @Override
    public List<Path> getMigratedFiles() {
        return migratedFiles;
    }

    @Override
    public void reportFileMigrated(Path path) {
        migratedFiles.add(path);
    }
}
