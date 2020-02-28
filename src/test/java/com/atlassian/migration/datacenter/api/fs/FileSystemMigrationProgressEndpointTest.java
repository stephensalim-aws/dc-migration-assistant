package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.api.fs.FileSystemMigrationProgressEndpoint.FSMigrationProgressWebObject;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus.RUNNING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileSystemMigrationProgressEndpointTest {

    @Mock
    private FilesystemMigrationService migrationService;

    @Mock
    private FileSystemMigrationReport report;

    private FileSystemMigrationProgressEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpoint = new FileSystemMigrationProgressEndpoint(migrationService);
    }

    @Test
    void shouldReturnReportWhenMigrationExists() {
        when(migrationService.getReport()).thenReturn(report);

        when(report.getStatus()).thenReturn(RUNNING);

        final String testReason = "test reason";
        final Path testFile = Paths.get("file");
        final FailedFileMigration failedFileMigration = new FailedFileMigration(testFile, testReason);
        when(report.getFailedFiles()).thenReturn(Collections.singletonList(failedFileMigration));

        final String successFileName = "success_file.txt";
        final Path successFile = Paths.get(successFileName);
        when(report.getMigratedFiles()).thenReturn(Collections.singletonList(successFile));

        final Response response = endpoint.getFilesystemMigrationStatus();

        final FSMigrationProgressWebObject returnedReport = (FSMigrationProgressWebObject) response.getEntity();

        assertEquals(RUNNING.name(), returnedReport.status);
        assertEquals(1, returnedReport.failedFileMigrations.size());
        assertEquals(testReason, returnedReport.failedFileMigrations.get(0).getReason());
        assertEquals(testFile, returnedReport.failedFileMigrations.get(0).getFilePath());
        assertEquals(1, returnedReport.migratedFiles.size());
        assertEquals(successFileName, returnedReport.migratedFiles.get(0).toString());
    }

    @Test
    void shouldReturnBadRequestWhenNoReportExists() {
        when(migrationService.getReport()).thenReturn(null);

        final Response response = endpoint.getFilesystemMigrationStatus();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("no file system migration exists", response.getEntity());
    }
}
