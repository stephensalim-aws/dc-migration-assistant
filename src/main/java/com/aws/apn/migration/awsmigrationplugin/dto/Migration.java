package com.aws.apn.migration.awsmigrationplugin.dto;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import net.java.ao.Entity;

public interface Migration extends Entity {

    MigrationStage getStage();

    void setStage(MigrationStage stage);

}
