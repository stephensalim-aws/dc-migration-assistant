package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionManagement;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/region")
public class AWSRegionEndpoint {

    private final RegionManagement regionManagement;

    @Autowired
    public AWSRegionEndpoint(RegionManagement regionManagement) {
        this.regionManagement = regionManagement;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getRegion() {
        return Response.ok(regionManagement.getRegion()).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response setRegion(AWSRegionWebObject region) {
        try {
            regionManagement.storeRegion(region.getRegion());
        } catch (InvalidAWSRegionException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @JsonAutoDetect
    @Data
    static class AWSRegionWebObject {
        private String region;
    }
}
