package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);
    private static final String DEFAULT_PREFIX = "";
    private static final boolean DEFAULT_INCLUDE_SUBDIRECTORIES = true;

    private FilesystemMigrationProgress progress = new FilesystemMigrationProgress(FilesystemMigrationStatus.NOT_STARTED);

    public void startMigration(FilesystemMigrationConfig config) {
        progress = new FilesystemMigrationProgress();
        TransferManager transferManager = getTransferManager();

        boolean canMigrate = verifyDirectory(config.getDirectoryToMigrate());

        if (canMigrate) {
            MultipleFileUpload upload = transferManager.uploadDirectory(
                    config.getS3Bucket(),
                    DEFAULT_PREFIX,
                    config.getDirectoryToMigrate().toFile(),
                    DEFAULT_INCLUDE_SUBDIRECTORIES);
            progress.setStatus(FilesystemMigrationStatus.RUNNING);
            S3UploadListener listener = new S3UploadListener(progress);
            upload.addProgressListener(listener);
            FilesystemMigrationStatus status = waitToComplete(upload);
            progress.setStatus(status);
        } else {
            progress.setStatus(FilesystemMigrationStatus.FAILED);
        }
    }

    @Override
    public FilesystemMigrationProgress getProgress() {
        return progress;
    }

    private FilesystemMigrationStatus waitToComplete(MultipleFileUpload upload) {
        try {
            upload.waitForCompletion();
        } catch (AmazonClientException e) {
            logger.error("Amazon service error: {}", e.getMessage());
            return FilesystemMigrationStatus.FAILED;
        } catch (InterruptedException e) {
            logger.error("Transfer interrupted: {}", e.getMessage());
            return FilesystemMigrationStatus.FAILED;
        }
        if (upload.isDone()) {
            logger.info("Finished transfering the files");
            return FilesystemMigrationStatus.DONE;
        } else {
            return FilesystemMigrationStatus.FAILED;
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
