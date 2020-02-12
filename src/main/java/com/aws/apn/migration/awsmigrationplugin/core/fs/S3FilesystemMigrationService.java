package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.api.fs.FilesystemMigrationStatus;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private String defaultPrefix = "";
    private boolean defaultIncludeSubDirs = true;

    public FilesystemMigrationProgress startMigration(FilesystemMigrationConfig config) {
        TransferManager transferManager = getTransferManager();

        boolean canMigrate = verifyDirectory(config.getDirectoryToMigrate());

        if (canMigrate) {
            MultipleFileUpload upload = transferManager.uploadDirectory(
                    config.getS3Bucket(),
                    defaultPrefix,
                    config.getDirectoryToMigrate().toFile(),
                    defaultIncludeSubDirs);
            upload.addProgressListener(new S3UploadListener());
            try {
                upload.waitForCompletion();
            } catch (AmazonClientException e) {
                logger.error("Amazon service error: {}", e.getMessage());
                return new FilesystemMigrationProgress(FilesystemMigrationStatus.FAILED);
            } catch (InterruptedException e) {
                logger.error("Transfer interrupted: {}", e.getMessage());
                return new FilesystemMigrationProgress(FilesystemMigrationStatus.FAILED);
            }
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
