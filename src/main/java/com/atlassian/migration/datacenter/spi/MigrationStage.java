package com.atlassian.migration.datacenter.spi;

/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    ERROR("error", null),
    FINISHED("finished", null),
    CUTOVER("cutover", FINISHED),
    VALIDATE("validate", CUTOVER),
    DB_MIGRATION_IMPORT("db_migration_down", VALIDATE), DB_MIGRATION_EXPORT("db_migration_up", DB_MIGRATION_IMPORT),
    OFFLINE_WARNING("cutover_warning", DB_MIGRATION_EXPORT),
    FS_MIGRATION_IMPORT("fs_migration_down", OFFLINE_WARNING), FS_MIGRATION_EXPORT("fs_migration_up", FS_MIGRATION_IMPORT), READY_FS_MIGRATION("ready_fs_migration", FS_MIGRATION_EXPORT),
    WAIT_PROVISION_MIGRATION_STACK("wait_provision_migration", READY_FS_MIGRATION), PROVISION_MIGRATION_STACK("provision_migration", WAIT_PROVISION_MIGRATION_STACK),
    WAIT_PROVISION_APPLICATION("wait_provision_app", PROVISION_MIGRATION_STACK), PROVISION_APPLICATION("provision_app", WAIT_PROVISION_APPLICATION),
    AUTHENTICATION("authentication", PROVISION_APPLICATION),
    NOT_STARTED("", AUTHENTICATION);

    private String key;
    private MigrationStage nextStage;

    MigrationStage(String key, MigrationStage nextStage) {
        this.key = key;
        this.nextStage = nextStage;
    }

    @Override
    public String toString() {
        return key;
    }

    public MigrationStage getNext() {
        return nextStage;
    }
}
