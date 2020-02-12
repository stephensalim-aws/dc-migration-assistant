package com.aws.apn.migration.awsmigrationplugin.spi.fs;

import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationProgress;

public interface FilesystemMigrationService {
    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config);
}
