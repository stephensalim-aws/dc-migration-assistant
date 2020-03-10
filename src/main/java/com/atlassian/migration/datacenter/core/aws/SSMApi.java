package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

public class SSMApi {

    private final AwsCredentialsProvider credentialsService;
    private final RegionService regionService;

    // This may be null, use getClient to safely get a non-null SSM client
    private SsmClient client;

    public SSMApi(AwsCredentialsProvider credentialsService, RegionService regionService) {
        this.credentialsService = credentialsService;
        this.regionService = regionService;
    }

    // Lazily instantiates the ssm client
    private SsmClient getClient() {
        if (client == null) {
            client = SsmClient.builder()
                    .credentialsProvider(credentialsService)
                    .region(Region.of(regionService.getRegion()))
                    .build();
        }

        return client;
    }
}
