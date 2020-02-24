package com.atlassian.migration.datacenter.api.aws;

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
 * REST API Endpoint for managing AWS provisioning.
 */
@Path("/aws/cloudformation")
public class CloudFormationEndpoint {

    private MigrationService migrationService;

    public CloudFormationEndpoint(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @POST
    @Path("/create")
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
    @Path("/{stackId}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfrastructureStatus(@PathParam("stackId") String stackId) {
        Optional<String> status = migrationService.getInfrastructureProvisioningStatus(stackId);
        if (status.isPresent()) {
            return Response.status(Response.Status.OK).entity(status.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
