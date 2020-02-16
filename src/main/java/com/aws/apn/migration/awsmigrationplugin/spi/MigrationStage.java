package com.aws.apn.migration.awsmigrationplugin.spi;

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    NOT_STARTED(""), STARTED("started"),
    READY_TO_PROVISION("ready_to_provision"),PROVISIONING_IN_PROGRESS("provisioning_in_progress"),PROVISIONING_COMPLETE("provisioning_complete"),PROVISIONING_ERROR("provisioning_error"),
    READY_FS_MIGRATION("ready_fs_migration"), FS_MIGRATION("fs_migration");

    private String key;

    MigrationStage(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
