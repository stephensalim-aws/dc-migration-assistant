package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/migration/fs")
public class FileSystemMigrationProgressEndpoint {

    private final FilesystemMigrationService migrationService;

    public FileSystemMigrationProgressEndpoint(FilesystemMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getFilesystemMigrationStatus() {
        FileSystemMigrationReport report = migrationService.getReport();
        if (report == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("no file system migration exists")
                    .build();
        }
        final FSMigrationProgressWebObject progress = new FSMigrationProgressWebObject(
                report.getStatus().name(),
                report.getFailedFiles(),
                report.getMigratedFiles().stream().map(java.nio.file.Path::toString).collect(Collectors.toList())
        );

        return Response
                .ok(progress)
                .build();
    }

    static class FSMigrationProgressWebObject {
        public String status;
        public List<FailedFileMigrationWebObject> failedFileMigrations;
        public List<String> migratedFiles;

        FSMigrationProgressWebObject(String status, List<FailedFileMigration> failedFileMigrations, List<String> migratedFiles) {
            this.status = status;
            this.migratedFiles = migratedFiles;

            this.failedFileMigrations = failedFileMigrations
                    .stream()
                    .map(failedFileMigration -> new FailedFileMigrationWebObject(failedFileMigration.getFilePath().toString(), failedFileMigration.getReason()))
                    .collect(Collectors.toList());
        }
    }

    static class FailedFileMigrationWebObject {
        public String path;
        public String reason;

        public FailedFileMigrationWebObject(String path, String reason) {
            this.path = path;
            this.reason = reason;
        }
    }
}
