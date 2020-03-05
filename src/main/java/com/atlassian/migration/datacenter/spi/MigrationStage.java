package com.atlassian.migration.datacenter.spi;

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    NOT_STARTED(""),
    ERROR("error"),
    AUTHENTICATION("authentication"),
    PROVISION_APPLICATION("provision_app"), WAIT_PROVISION_APPLICATION("wait_provision_app"),
    PROVISION_MIGRATION_STACK("provision_migration"), WAIT_PROVISION_MIGRATION_STACK("wait_provision_migration"),
    READY_FS_MIGRATION("ready_fs_migration"), FS_MIGRATION_UP("fs_migration_up"), FS_MIGRATION_DOWN("fs_migration_down");

    private String key;

    MigrationStage(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
