package com.atlassian.migration.datacenter.api.fs;

import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FailedFileMigration;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationReport;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public Response getFilesystemMigrationStatus() throws JsonProcessingException {
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        return Response
                .ok(mapper.writeValueAsString(progress))
                .build();
    }

    @JsonAutoDetect
    public static class FSMigrationProgressWebObject {
        public String status;
        public List<FailedFileMigration> failedFileMigrations;

        public String getStatus() {
            return status;
        }

        public List<FailedFileMigration> getFailedFileMigrations() {
            return failedFileMigrations;
        }

        public List<String> getMigratedFiles() {
            return migratedFiles;
        }

        public List<String> migratedFiles;

        FSMigrationProgressWebObject(String status, List<FailedFileMigration> failedFileMigrations, List<String> migratedFiles) {
            this.status = status;
            this.failedFileMigrations = failedFileMigrations;
            this.migratedFiles = migratedFiles;
        }
    }
}
