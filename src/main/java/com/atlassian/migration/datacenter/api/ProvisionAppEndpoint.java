package com.atlassian.migration.datacenter.api;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.infrastructure.AppProvisioningService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/migration/provision/app")
public class ProvisionAppEndpoint {

    AppProvisioningService appProvisioningService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deployAppStack(ProvisioningConfig config) {
        try {
            appProvisioningService.provisionApp(config);
            return Response.ok().build();
        } catch (InvalidMigrationStageError invalidMigrationStageError) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(invalidMigrationStageError.getMessage())
                    .build();
        }
    }
}
