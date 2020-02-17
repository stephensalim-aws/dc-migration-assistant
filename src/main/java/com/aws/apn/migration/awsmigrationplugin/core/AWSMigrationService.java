package com.aws.apn.migration.awsmigrationplugin.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.aws.apn.migration.awsmigrationplugin.core.aws.CfnApi;
import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InfrastructureProvisioningError;
import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InvalidMigrationStageError;
import com.aws.apn.migration.awsmigrationplugin.dto.Migration;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.infrastructure.ProvisioningConfig;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.FS_MIGRATION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.NOT_STARTED;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.READY_FS_MIGRATION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.READY_TO_PROVISION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.STARTED;
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
        if (getMigrationStage() != NOT_STARTED) {
            return false;
        }

        updateMigrationStage(STARTED);

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
                migration.setStage(NOT_STARTED);
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
        if (currentMigrationStage != READY_TO_PROVISION) {
            throw new InvalidMigrationStageError(String.format("Expected migration stage was %s, but found %s", READY_TO_PROVISION, currentMigrationStage));
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
        if (getMigrationStage() != READY_FS_MIGRATION) {
            return false;
        }
        updateMigrationStage(FS_MIGRATION);
        fsService.startMigration(config);
        return true;
    }
}
