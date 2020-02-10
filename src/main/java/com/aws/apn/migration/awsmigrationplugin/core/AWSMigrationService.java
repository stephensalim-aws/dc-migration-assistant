package com.aws.apn.migration.awsmigrationplugin.core;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;

public class AWSMigrationService implements MigrationService {

    @Override
    public void startMigration() {

    }

    @Override
    public MigrationStage getMigrationStage() {
        return null;
    }
}
