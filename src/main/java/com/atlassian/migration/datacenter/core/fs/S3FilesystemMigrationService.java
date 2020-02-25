package com.atlassian.migration.datacenter.core.fs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private static final String S3_PREFIX = "";
    private static final boolean INCLUDE_SUBDIRECTORIES = true;
    public static final int NUM_UPLOAD_THREADS = 2;

    private final AtlassianPluginAWSCredentialsProvider credentialsProvider;
    private final RegionService regionService;

    private MultipleFileUpload upload;
    private FilesystemMigrationProgress progress = new FilesystemMigrationProgress(FilesystemMigrationStatus.NOT_STARTED);

    public S3FilesystemMigrationService(RegionService regionService, AtlassianPluginAWSCredentialsProvider credentialsProvider) {
        this.regionService = regionService;
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob
     */
    public void startMigration() {
        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
        S3UploadConfig config = new S3UploadConfig(getS3Bucket(), s3AsyncClient, getSharedHomeDir());

        Crawler homeCrawler = new DirectoryStreamCrawler();
        ExecutorService service = Executors.newSingleThreadExecutor();

        ConcurrentLinkedQueue uploadQueue = new ConcurrentLinkedQueue();

        service.submit(() -> {
            try {
                homeCrawler.crawlDirectory(getSharedHomeDir(), uploadQueue);
            } catch (IOException e) {
                logger.error("Failed to traverse home directory for S3 transfer", e);
                progress.setStatus(FilesystemMigrationStatus.FAILED);
            }
        });


        ExecutorService uploadService = Executors.newFixedThreadPool(NUM_UPLOAD_THREADS);
        IntStream.range(0, NUM_UPLOAD_THREADS).forEach(i -> {
            uploadService.submit(() -> {

            });
        });
    }

    private String getS3Bucket() {
        return "slingshot-test-2";
    }

    private Path getSharedHomeDir() {
        return Paths.get("/fake/shared/home");
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
