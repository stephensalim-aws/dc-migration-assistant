package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationStatus;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private String defaultPrefix = "";
    private boolean defaultIncludeSubDirs = true;

    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config) {
        TransferManager transferManager = getTransferManager();

        boolean canMigrate = verifyDirectory(config.getDirectoryToMigrate());

        if (canMigrate) {
            transferManager.uploadDirectory(
                    config.getS3Bucket(),
                    defaultPrefix,
                    config.getDirectoryToMigrate().toFile(),
                    defaultIncludeSubDirs);
            return new FilesystemMigrationProgress(FilesystemMigrationStatus.RUNNING);
        } else {
            return new FilesystemMigrationProgress(FilesystemMigrationStatus.FAILED);
        }
    }

    private boolean verifyDirectory(Path directoryToMigrate) {
        // Simple check for now, can be extended to verify home structure for different products
        return directoryToMigrate.toFile().exists();
    }

    private TransferManager getTransferManager() {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        return TransferManagerBuilder.standard().withS3Client(s3).build();
    }
}
