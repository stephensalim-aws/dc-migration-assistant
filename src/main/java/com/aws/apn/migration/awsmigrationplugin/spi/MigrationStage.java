package com.aws.apn.migration.awsmigrationplugin.spi;

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
