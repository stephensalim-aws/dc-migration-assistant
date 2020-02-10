package com.aws.apn.migration.awsmigrationplugin.spi;

public interface MigrationService {

    boolean startMigration();

    MigrationStage getMigrationStage();

}
