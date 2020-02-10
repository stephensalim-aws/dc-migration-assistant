package com.aws.apn.migration.awsmigrationplugin.core;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.STARTED;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.UNSTARTED;

public class AWSMigrationService implements MigrationService {

    private MigrationStage currentStage;

    public AWSMigrationService() {
        this.currentStage = UNSTARTED;
    }

    @Override
    public boolean startMigration() {
        this.currentStage = STARTED;
        return true;
    }

    @Override
    public MigrationStage getMigrationStage() {
        return currentStage;
    }
}
