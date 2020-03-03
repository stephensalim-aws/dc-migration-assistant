package com.atlassian.migration.datacenter.spi.infrastructure;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;

public interface AppProvisioningService {

    void provisionApp(ProvisioningConfig provisioningConfig) throws InvalidMigrationStageError;

    double getProvisioningProgress();

}
