package com.atlassian.migration.datacenter.api.aws;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AWSRegionEndpointTest {


    @Mock
    private AvailabilityZoneService mockAzService;

    @Mock
    private RegionService regionService;

    @InjectMocks
    private AWSRegionEndpoint endpoint;


    @Test
    public void testOKAZList() {
        List<AvailabilityZone> azList = new ArrayList<>();
        azList.add(new AvailabilityZone().withZoneName("eu-central-1a").withZoneId("euc1-az2"));
        azList.add(new AvailabilityZone().withZoneName("eu-central-1b").withZoneId("euc1-az3"));
        azList.add(new AvailabilityZone().withZoneName("eu-central-1c").withZoneId("euc1-az1"));

        try {
            when(mockAzService.getZonesForCurrentRegion()).thenReturn(azList);
            Response response = endpoint.getCurrentRegionAZList();
            assertEquals(3, ((List<AWSRegionEndpoint.AWSAZWebObject>) response.getEntity()).size());
        } catch (InvalidAWSRegionException invalidRegionException) {
            fail(invalidRegionException.getMessage());
        }


    }

    @Test
    public void testBadAZList() {
        Response response = null;
        try {
            when(mockAzService.getZonesForCurrentRegion()).thenThrow(new InvalidAWSRegionException());
            response = endpoint.getCurrentRegionAZList();
        } catch (InvalidAWSRegionException invalidRegionException) {
            Assert.assertNull(response);
        }
    }

}
