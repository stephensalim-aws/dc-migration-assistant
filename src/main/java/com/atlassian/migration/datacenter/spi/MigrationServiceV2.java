package com.atlassian.migration.datacenter.spi;

public interface MigrationServiceV2 {

    MigrationStage getCurrentStage();

    void setCurrentStage(MigrationStage stage);

}
