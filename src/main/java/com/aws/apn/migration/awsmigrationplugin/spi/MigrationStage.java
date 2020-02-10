package com.aws.apn.migration.awsmigrationplugin.spi;

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    UNSTARTED(""), STARTED("started");

    private String key;

    MigrationStage(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
