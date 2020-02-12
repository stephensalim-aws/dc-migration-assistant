package com.aws.apn.migration.awsmigrationplugin.spi;

import com.atlassian.activeobjects.tx.Transactional;
import com.aws.apn.migration.awsmigrationplugin.core.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.core.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;

/**
 * Abstraction of an on-premise to cloud migration modeled as a finite state machine.
 */
@Transactional
public interface MigrationService {

    /**
     * Tries to begin an on-premise to cloud migration. The migration will only be created if a migration doesn't exist.
     *
     * @return true if the migration was created, false otherwise.
     */
    boolean startMigration();

    /**
     * @return the stage that the current migration is up to.
     * @see MigrationStage
     */
    MigrationStage getMigrationStage();

    /**
     * Start migration of local files to remote destination
     */
    FilesystemMigrationProgress startFilesystemMigration(FilesystemMigrationService service, FilesystemMigrationConfig config);
}
