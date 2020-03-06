package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.Migration;

public interface MigrationServiceV2 {

    Migration createMigration();

    MigrationStage getCurrentStage();

    void transition(MigrationStage from, MigrationStage to) throws InvalidMigrationStageError;

    void error();

}
