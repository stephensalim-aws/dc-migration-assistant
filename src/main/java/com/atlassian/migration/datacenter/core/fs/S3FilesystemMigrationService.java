package com.atlassian.migration.datacenter.core.fs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationConfig;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger log = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private static final String S3_PREFIX = "";
    private static final boolean INCLUDE_SUBDIRECTORIES = true;

    private MultipleFileUpload upload;
    private FilesystemMigrationProgress progress = new FilesystemMigrationProgress(FilesystemMigrationStatus.NOT_STARTED);
    private TransferManager transferManager;

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob
     *
     * @param config configuration for filesystem migration
     */
    public void startMigration(FilesystemMigrationConfig config) {
        if (transferManager == null) {
            transferManager = createTransferManager();
        }

        final Path path = Paths.get(config.getDirectory());
        if (isValid(path)) {
            log.debug("Start uploading directory {}", config.getDirectory());

            // FIXME - this execution currently consumes large amount of memory as `TransferManager` loads all files into memory
            upload = transferManager.uploadDirectory(
                    config.getS3Bucket(),
                    S3_PREFIX,
                    path.toFile(),
                    INCLUDE_SUBDIRECTORIES);

            progress.setStatus(FilesystemMigrationStatus.RUNNING);
            log.debug("Adding S3 progress listener");
            upload.addProgressListener(new S3UploadListener(progress));
            // this is a blocking operation
            waitForUpload();
        } else {
            log.error("Path to upload is incorrect: {}", path);
            progress.setStatus(FilesystemMigrationStatus.FAILED);
        }
    }

    private void waitForUpload() {
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            log.error("S3 upload interrupted", e);
            progress.setStatus(FilesystemMigrationStatus.FAILED);
        } catch (AmazonServiceException e) {
            log.error("S3 upload failed - Amazon service experienced issue", e);
            progress.setStatus(FilesystemMigrationStatus.FAILED);
        } catch (AmazonClientException e) {
            log.error("S3 upload failed - Amazon client had a problem", e);
            progress.setStatus(FilesystemMigrationStatus.FAILED);
        }
        progress.setStatus(FilesystemMigrationStatus.DONE);
    }

    @Override
    public FilesystemMigrationProgress getProgress() {
        return progress;
    }

    public boolean isRunning() {
        return upload != null && !upload.isDone();
    }

    private boolean isValid(Path directoryToMigrate) {
        // Simple check for now, can be extended to verify home structure for different products
        return directoryToMigrate.toFile().exists();
    }

    private TransferManager createTransferManager() {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        return TransferManagerBuilder.standard().withS3Client(s3).build();
    }
}
