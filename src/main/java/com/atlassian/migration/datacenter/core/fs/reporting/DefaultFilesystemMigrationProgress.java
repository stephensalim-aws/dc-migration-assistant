package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultFilesystemMigrationProgress implements FileSystemMigrationProgress {

    private ConcurrentHashMap<Path, Void> migratedFiles = new ConcurrentHashMap<>();

    private AtomicLong filesFound = new AtomicLong(0);

    private AtomicLong filesInFlight = new AtomicLong(0);

    @Override
    public Long getNumberOfFilesFound() {
        return filesFound.get();
    }

    @Override
    public void reportFileFound() {
        filesFound.incrementAndGet();
    }

    @Override
    public Long getNumberOfFilesInFlight() {
        return filesInFlight.get();
    }

    @Override
    public void reportFileInFlight() {
        filesInFlight.incrementAndGet();
    }

    @Override
    public Set<Path> getMigratedFiles() {
        return ImmutableSet.copyOf(migratedFiles.keySet());
    }

    @Override
    public void reportFileMigrated(Path path) {
        migratedFiles.put(path, null);
        filesInFlight.decrementAndGet();
    }
}
