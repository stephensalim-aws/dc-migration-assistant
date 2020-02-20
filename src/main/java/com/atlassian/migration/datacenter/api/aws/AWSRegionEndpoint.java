package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/region")
public class AWSRegionEndpoint {

    private final RegionService regionService;

    @Autowired
    public AWSRegionEndpoint(RegionService regionService) {
        this.regionService = regionService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getRegion() {
        return Response.ok(regionService.getRegion()).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response setRegion(AWSRegionWebObject region) {
        try {
            regionService.storeRegion(region.getRegion());
        } catch(InvalidAWSRegionException e) {
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
    static class AWSRegionWebObject {

        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion() {
            this.region = region;
        }
    }
}
