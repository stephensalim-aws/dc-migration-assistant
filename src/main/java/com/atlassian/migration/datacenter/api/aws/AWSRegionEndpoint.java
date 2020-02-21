package com.atlassian.migration.datacenter.api.aws;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/region")
public class AWSRegionEndpoint {

    private final RegionService regionService;
    private final AvailabilityZoneService availabilityZoneService;

    @Inject
    public AWSRegionEndpoint(RegionService regionService, AvailabilityZoneService availabilityZoneService) {
        this.regionService = regionService;
        this.availabilityZoneService = availabilityZoneService;
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

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/availabilityZones")
    public Response getCurrentRegionAZList() throws InvalidAWSRegionException {
        List<AvailabilityZone> zones = this.availabilityZoneService.getZonesForCurrentRegion();
        try (Stream<AvailabilityZone> zoneStream = zones.parallelStream()) {
            List<AWSAZWebObject> dtoList = zoneStream.map(zone -> new AWSAZWebObject(zone.getZoneName(), zone.getZoneId()))
                    .sorted()
                    .collect(Collectors.toList());
            return Response.ok(dtoList).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
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

    @Data
    @AllArgsConstructor
    @JsonAutoDetect
    static class AWSAZWebObject implements Comparable<AWSAZWebObject> {
        private String name;
        private String id;

        @Override
        public int compareTo(AWSAZWebObject o) {
            return this.name.compareTo(o.getName());
        }
    }
}
