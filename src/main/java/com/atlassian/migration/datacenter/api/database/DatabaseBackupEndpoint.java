package com.atlassian.migration.datacenter.api.database;

import com.atlassian.migration.datacenter.core.database.DatabaseBackupService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("database/backup")
public class DatabaseBackupEndpoint {

    private final DatabaseBackupService databaseBackupService;

    @Inject
    public DatabaseBackupEndpoint(DatabaseBackupService databaseBackupService) {
        this.databaseBackupService = databaseBackupService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response backupDatabase() {
        if (this.databaseBackupService.dumpDatabaseToFile()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
