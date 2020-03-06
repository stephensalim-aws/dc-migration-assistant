package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.cloud.CloudConfigurationService;

public class AWSConfigurationService implements CloudConfigurationService {

    private WriteCredentialsService writeCredentialsService;
    private RegionService regionService;

    @Override
    public void configureCloudProvider(String entity, String secret, String geography) {
        writeCredentialsService.storeAccessKeyId(entity);
        writeCredentialsService.storeSecretAccessKey(secret);
        try {
            regionService.storeRegion(geography);
        } catch (InvalidAWSRegionException e) {
            throw new RuntimeException(e);
        }
    }
}
