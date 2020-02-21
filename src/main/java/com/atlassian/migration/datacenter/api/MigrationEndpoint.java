package com.atlassian.migration.datacenter.api;

import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationConfig;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

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
     *
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

    @GET
    @Path("/provision/{stackId}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfrastructureStatus(@PathParam("stackId") String stackId) {
        Optional<String> status = migrationService.getInfrastructureProvisioningStatus(stackId);
        if (status.isPresent()) {
            return Response.status(Response.Status.OK).entity(status.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/filesystem")
    public Response runFileMigration(FilesystemMigrationConfig config) {
        boolean started = migrationService.startFilesystemMigration(config);
        return Response.status(Response.Status.ACCEPTED).entity("S3 Migration started = " + started).build();
    }
}
