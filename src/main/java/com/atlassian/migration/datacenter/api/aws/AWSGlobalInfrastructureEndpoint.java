package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/aws/global-infrastructure")
public class AWSGlobalInfrastructureEndpoint {

    private GlobalInfrastructure globalInfrastructure;

    public AWSGlobalInfrastructureEndpoint(GlobalInfrastructure globalInfrastructure) {
        this.globalInfrastructure = globalInfrastructure;
    }

    /**
     * @return A response with all AWS regions
     */
    @GET
    @Path("/regions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegions() {
        List<String> regions = globalInfrastructure.getRegions();
        if (regions == null) {
            return Response
                    .serverError()
                    .build();
        }

        return Response
                .ok(regions)
                .build();
    }
}
