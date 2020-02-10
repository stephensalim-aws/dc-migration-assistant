package com.aws.apn.migration.awsmigrationplugin.api;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/migration")
public class MigrationEndpoint {

    private MigrationService migrationService;

    public MigrationEndpoint(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMigration() {
        if (migrationService.getMigrationStage() == MigrationStage.UNSTARTED) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        } else {
            return Response
                    .ok(migrationService.getMigrationStage().toString())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMigration() {
        if (migrationService.startMigration()) {
            return Response
                    .ok()
                    .build();
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("migration already exists")
                    .build();
        }
    }
}
