package com.atlassian.migration.datacenter.core.aws.region;

import com.amazonaws.services.ec2.model.AvailabilityZone;

import java.util.List;

public interface AvailabilityZoneService {

    List<AvailabilityZone> getZonesForRegion(String region);

}
