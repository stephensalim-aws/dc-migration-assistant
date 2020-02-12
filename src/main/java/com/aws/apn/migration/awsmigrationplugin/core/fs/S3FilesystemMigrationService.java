package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config) {
        return new FilesystemMigrationProgress(FilesystemMigrationStatus.RUNNING);
    }
}
