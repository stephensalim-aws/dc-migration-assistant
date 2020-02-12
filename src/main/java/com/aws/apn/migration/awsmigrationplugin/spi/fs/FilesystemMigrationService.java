package com.aws.apn.migration.awsmigrationplugin.spi.fs;

import com.aws.apn.migration.awsmigrationplugin.core.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.core.fs.FilesystemMigrationProgress;

public interface FilesystemMigrationService {
    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config);
}
