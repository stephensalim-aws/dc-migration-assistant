package com.atlassian.migration.datacenter.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationConfig;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
@Component
public class AWSMigrationService implements MigrationService {

    private ActiveObjects ao;

    private FilesystemMigrationService fsService;
    private final CfnApi cfnApi;

    private Migration migration;


    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(@ComponentImport ActiveObjects ao, FilesystemMigrationService fms, CfnApi cfnApi) {
        this.ao = requireNonNull(ao);
        this.fsService = fms;
        this.cfnApi = cfnApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startMigration() {
        if (getMigrationStage() != MigrationStage.NOT_STARTED) {
            return false;
        }

        updateMigrationStage(MigrationStage.STARTED);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MigrationStage getMigrationStage() {
        if (migration == null) {
            Migration[] migrations = ao.find(Migration.class);
            if (migrations.length == 1) {
                // In case we have interrupted migration (e.g. the node went down), we want to pick up where we've
                // left off.
                migration = migrations[0];
            } else {
                // We didn't start the migration, so we need to create record in the db
                migration = ao.create(Migration.class);
                migration.setStage(MigrationStage.NOT_STARTED);
                migration.save();
            }
        }
        return migration.getStage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provisionInfrastructure(ProvisioningConfig config) {
        //TODO: Refactor this to a state machine as part of https://aws-partner.atlassian.net/browse/CHET-101
        MigrationStage currentMigrationStage = getMigrationStage();
        if (currentMigrationStage != MigrationStage.READY_TO_PROVISION) {
            throw new InvalidMigrationStageError(String.format("Expected migration stage was %s, but found %s", MigrationStage.READY_TO_PROVISION, currentMigrationStage));
        }

        Optional<String> stackIdentifier = this.cfnApi.provisionStack(config.getTemplateUrl(), config.getStackName(), config.getParams());
        if (stackIdentifier.isPresent()) {
            updateMigrationStage(MigrationStage.PROVISIONING_IN_PROGRESS);
            return stackIdentifier.get();
        } else {
            updateMigrationStage(MigrationStage.PROVISIONING_ERROR);
            throw new InfrastructureProvisioningError(String.format("Unable to provision stack (URL - %s) with name - %s", config.getTemplateUrl(), config.getStackName()));
        }
    }

    public void updateMigrationStage(MigrationStage stage) {
        migration.setStage(stage);
        migration.save();
    }

    public boolean startFilesystemMigration(FilesystemMigrationConfig config) {
        if (getMigrationStage() != MigrationStage.READY_FS_MIGRATION) {
            return false;
        }
        updateMigrationStage(MigrationStage.FS_MIGRATION);
        fsService.startMigration(config);
        return true;
    }
}
