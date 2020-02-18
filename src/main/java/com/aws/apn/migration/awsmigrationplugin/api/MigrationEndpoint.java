package com.aws.apn.migration.awsmigrationplugin.api;

import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InfrastructureProvisioningError;
import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InvalidMigrationStageError;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import com.aws.apn.migration.awsmigrationplugin.spi.infrastructure.ProvisioningConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST API Endpoint for managing in-product DC migrations.
 * Supports get and create.
 */
@Path("/migration")
public class MigrationEndpoint {

    private MigrationService migrationService;

    public MigrationEndpoint(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    /**
     * @return A response with the status of the current migration
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMigrationStatus() {
        if (migrationService.getMigrationStage() == MigrationStage.NOT_STARTED) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        } else {
            return Response
                    .ok(migrationService.getMigrationStage().toString())
                    .build();
        }
    }

    /**
     * Creates a new migration if none exists. Otherwise will respond with a 400 and an error message.
     * @return no content if successful or 400 and error  message if a migration already exists.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMigration() {
        if (migrationService.startMigration()) {
            return Response
                    .noContent()
                    .build();
        } else {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("migration already exists")
                    .build();
        }
    }

    @POST
    @Path("/provision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response provisionInfrastructure(ProvisioningConfig provisioningConfig) {
        try {
            String stackId = migrationService.provisionInfrastructure(provisioningConfig);
            //Should be updated to URI location after get stack details Endpoint is built
            return Response.status(Response.Status.ACCEPTED).entity(stackId).build();
        } catch (InvalidMigrationStageError | InfrastructureProvisioningError e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
