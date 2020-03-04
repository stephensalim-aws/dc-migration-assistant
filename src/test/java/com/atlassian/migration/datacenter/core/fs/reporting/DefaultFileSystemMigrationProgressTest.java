package com.atlassian.migration.datacenter.core.fs.reporting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultFileSystemMigrationProgressTest {

    private DefaultFilesystemMigrationProgress sut;

    @BeforeEach
    void setUp() {
        sut = new DefaultFilesystemMigrationProgress();
    }

    @Test
    void shouldBeInitialisedWithNoCompleteFiles() {
        assertEquals(0, sut.getCountOfMigratedFiles());
    }

    @Test
    void shouldAddMigratedFileToMigratedFiles() {
        final Path testFile = Paths.get("file");
        sut.reportFileMigrated();

        assertEquals(1, sut.getCountOfMigratedFiles());
    }

    @Test
    void shouldCountMigratingFile() {
        sut.reportFileFound();

        assertEquals(1, sut.getNumberOfFilesFound());
    }

    @Test
    void shouldCountInFlightFile() {
        sut.reportFileInFlight();

        assertEquals(1, sut.getNumberOfFilesInFlight());
    }

    @Test
    void ShouldRemoveFileFromInFlightWhenItIsMigrated() {
        sut.reportFileInFlight();
        sut.reportFileInFlight();

        assertEquals(2, sut.getNumberOfFilesInFlight());

        sut.reportFileMigrated(Paths.get("test"));

        assertEquals(1, sut.getNumberOfFilesInFlight());
    }
}
