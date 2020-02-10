package com.aws.apn.migration.awsmigrationplugin.spi;

public interface MigrationService {

    void startMigration();

    MigrationStage getMigrationStage();

}
