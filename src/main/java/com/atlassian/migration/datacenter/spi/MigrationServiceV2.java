package com.atlassian.migration.datacenter.spi;

import com.atlassian.migration.datacenter.dto.Migration;

public interface MigrationServiceV2 {

    Migration createMigration();

    MigrationStage getCurrentStage();

    void nextStage();

}
