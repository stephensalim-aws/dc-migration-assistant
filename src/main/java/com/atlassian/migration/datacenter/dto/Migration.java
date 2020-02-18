package com.atlassian.migration.datacenter.dto;

import com.atlassian.migration.datacenter.spi.MigrationStage;
import net.java.ao.Entity;

public interface Migration extends Entity {

    MigrationStage getStage();

    void setStage(MigrationStage stage);

}
