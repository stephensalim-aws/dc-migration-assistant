package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.Migration;

/**
 * Manages the lifecycle of the migration
 */
public interface MigrationServiceV2 {

    /**
     * Creates a new migration in the initial stage
     */
    Migration createMigration();

    /**
     * Gets the current stage of the migration
     */
    MigrationStage getCurrentStage();

    void transition(MigrationStage from, MigrationStage to) throws InvalidMigrationStageError;

    /**
     * Moves the migration into an error stage
     * @see MigrationStage#ERROR
     */
    void error();

}
