package com.atlassian.migration.datacenter.core.aws.region;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class PluginAvailabilityZoneService implements AvailabilityZoneService {

    private final RegionService regionService;
    private final AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Inject
    public PluginAvailabilityZoneService(RegionService regionService, AtlassianPluginAWSCredentialsProvider credentialsProvider) {
        this.regionService = regionService;
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public List<AvailabilityZone> getZonesForRegion(String region) {
        AmazonEC2 ec2Client = this.getEC2Client();
        List<AvailabilityZone> zones = ec2Client.describeAvailabilityZones().getAvailabilityZones();
        return zones;
    }

    private AmazonEC2 getEC2Client() {
        return AmazonEC2ClientBuilder.standard().withRegion(this.regionService.getRegion()).withCredentials(this.credentialsProvider).build();
    }
}
