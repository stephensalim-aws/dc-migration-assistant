package com.atlassian.migration.datacenter.core.fs.reporting;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.NOT_STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultFileSystemMigrationReportTest {

    private DefaultFileSystemMigrationReport sut;


    @Spy
    FileSystemMigrationProgress progress;

    @Spy
    FileSystemMigrationErrorReport errors;

    @BeforeEach
    void setUp() {
        sut = new DefaultFileSystemMigrationReport(errors, progress);
    }

    @Test
    void testStatusInitiallyNotStarted() {
        assertEquals(NOT_STARTED, sut.getStatus());
    }

    @Test
    void testSetsStatus() {
        sut.setStatus(FAILED);

        assertEquals(FAILED, sut.getStatus());
    }

    @Test
    void shouldDelegatesToWrappedErrorReport() {
        final Path testFile = Paths.get("file");
        final String testReason = "test";
        final FailedFileMigration failedFileMigration = new FailedFileMigration(testFile, testReason);
        sut.reportFileNotMigrated(failedFileMigration);

        verify(errors).reportFileNotMigrated(failedFileMigration);

        sut.getFailedFiles();
        verify(errors).getFailedFiles();
    }

    @Test
    void shouldDelegateToWrappedProgress() {
        final Path path = Paths.get("file");
        sut.reportFileMigrated(path);

        verify(progress).reportFileMigrated(path);

        sut.getMigratedFiles();

        verify(progress).getMigratedFiles();
    }
}
