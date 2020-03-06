package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.cloud.CloudConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSConfigurationService implements CloudConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(AWSConfigurationService.class);

    private WriteCredentialsService writeCredentialsService;
    private RegionService regionService;
    private MigrationServiceV2 migrationService;

    @Override
    public void configureCloudProvider(String entity, String secret, String geography) throws InvalidMigrationStageError {
        final MigrationStage currentStage = migrationService.getCurrentStage();
        if (!currentStage.equals(MigrationStage.AUTHENTICATION)) {
            logger.error("tried to configure AWS when in invalid stage {}", currentStage);
            throw new InvalidMigrationStageError("expected to be in stage " + MigrationStage.AUTHENTICATION + " but was in " + currentStage);
        }

        try {
            regionService.storeRegion(geography);
            logger.info("stored aws region");
        } catch (InvalidAWSRegionException e) {
            logger.error("error storing AWS region", e);
            throw new RuntimeException(e);
        }

        logger.info("Storing AWS credentials");
        writeCredentialsService.storeAccessKeyId(entity);
        writeCredentialsService.storeSecretAccessKey(secret);
    }
}
