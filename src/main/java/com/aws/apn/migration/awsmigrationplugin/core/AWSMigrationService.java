package com.aws.apn.migration.awsmigrationplugin.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationStatus;
import com.aws.apn.migration.awsmigrationplugin.dto.Migration;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import org.springframework.stereotype.Component;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.NOT_STARTED;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.READY_FS_MIGRATION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.STARTED;
import static java.util.Objects.requireNonNull;

/**
 * Manages a migration from on-premise to self-hosted AWS.
 */
@Component
@ExportAsService({MigrationService.class})
public class AWSMigrationService implements MigrationService {

    private ActiveObjects ao;

    private MigrationStage currentStage;

    /**
     * Creates a new, unstarted AWS Migration
     */
    public AWSMigrationService(@ComponentImport ActiveObjects ao) {
        this.ao = requireNonNull(ao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startMigration() {
        if (getMigrationStage() != NOT_STARTED) {
            return false;
        }

        final Migration migration = ao.create(Migration.class);
        migration.setStage(STARTED);
        migration.save();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MigrationStage getMigrationStage() {
        Migration[] migrations = ao.find(Migration.class);
        if (migrations.length == 1) {
            return migrations[0].getStage();
        }
        return NOT_STARTED;
    }

    @Override
    public FilesystemMigrationProgress startFilesystemMigration(@ComponentImport FilesystemMigrationService service, FilesystemMigrationConfig config) {
        if (this.getMigrationStage() != READY_FS_MIGRATION) {
            return new FilesystemMigrationProgress(FilesystemMigrationStatus.FAILED);
        }
        return service.startMigration(config);
    }
}
