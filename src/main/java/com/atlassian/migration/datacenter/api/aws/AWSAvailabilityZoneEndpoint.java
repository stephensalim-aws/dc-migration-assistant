package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("aws/availabilityZones")
public class AWSAvailabilityZoneEndpoint {

    private final AvailabilityZoneService availabilityZoneService;
    private final RegionService regionService;

    public AWSAvailabilityZoneEndpoint(AvailabilityZoneService availabilityZoneService, RegionService regionService) {
        this.availabilityZoneService = availabilityZoneService;
        this.regionService = regionService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailabilityZoneList() {
        try {
            List<AvailabilityZone> zonesList = this.availabilityZoneService.getAZForRegion(Region.of(this.regionService.getRegion()));

            try (Stream<AvailabilityZone> azStream = zonesList.parallelStream()) {
                List<String> nameList = azStream.map(AvailabilityZone::zoneName).sorted().collect(Collectors.toList());
                return Response.status(Response.Status.OK).entity(nameList).build();
            }

        } catch (InvalidAWSRegionException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
