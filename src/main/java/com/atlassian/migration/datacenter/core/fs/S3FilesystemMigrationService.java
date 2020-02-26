package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationProgress;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus.DONE;
import static com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus.FAILED;
import static com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus.RUNNING;

@Component
public class S3FilesystemMigrationService implements FilesystemMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(S3FilesystemMigrationService.class);

    private static final int NUM_UPLOAD_THREADS = Integer.getInteger("NUM_UPLOAD_THREADS", 2);
    private static final String BUCKET_NAME = System.getProperty("S3_TARGET_BUCKET_NAME", "slingshot-test-2");

    private final AwsCredentialsProvider credentialsProvider;
    private final RegionService regionService;
    private final JiraHome jiraHome;

    private FilesystemMigrationProgress progress = new FilesystemMigrationProgress(FilesystemMigrationStatus.NOT_STARTED);

    public S3FilesystemMigrationService(RegionService regionService,
                                        AwsCredentialsProvider credentialsProvider,
                                        @ComponentImport JiraHome jiraHome) {
        this.regionService = regionService;
        this.credentialsProvider = credentialsProvider;
        this.jiraHome = jiraHome;
    }

    /**
     * Start filesystem migration to S3 bucket. This is a blocking operation and should be started from ExecutorService
     * or preferably from ScheduledJob
     */
    public void startMigration() {
        progress.setStatus(RUNNING);

        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(regionService.getRegion()))
                .build();
        S3UploadConfig config = new S3UploadConfig(getS3Bucket(), s3AsyncClient, getSharedHomeDir());

        AtomicBoolean isDoneCrawling = new AtomicBoolean(false);
        ConcurrentLinkedQueue<Path> uploadQueue = new ConcurrentLinkedQueue<>();
        Queue<Future<?>> uploadResults = new LinkedList<>();
        ExecutorService uploadService = Executors.newFixedThreadPool(NUM_UPLOAD_THREADS);

        IntStream.range(0, NUM_UPLOAD_THREADS).forEach(i -> {
            Uploader uploader = new S3Uploader(config);
            Future<?> uploadResult = uploadService.submit(() -> {
                uploader.upload(uploadQueue, isDoneCrawling);
                // TODO surface failed uploads
            });
            uploadResults.add(uploadResult);
        });

        Crawler homeCrawler = new DirectoryStreamCrawler();
        try {
            homeCrawler.crawlDirectory(getSharedHomeDir(), uploadQueue);
        } catch (IOException e) {
            logger.error("Failed to traverse home directory for S3 transfer", e);
            progress.setStatus(FAILED);
        } finally {
            isDoneCrawling.set(true);
        }

        while (!uploadResults.isEmpty()) {
            Future<?> uploadResult = uploadResults.poll();
            try {
                uploadResult.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Failed to upload home directory to S3", e);
                progress.setStatus(FAILED);
                return;
            }
        }
        if (progress.getStatus() != FAILED) {
            progress.setStatus(DONE);
        }
    }

    private String getS3Bucket() {
        return BUCKET_NAME;
    }

    private Path getSharedHomeDir() {
        return jiraHome.getHome().toPath();
    }

    @Override
    public FilesystemMigrationProgress getProgress() {
        return progress;
    }

    @Override
    public boolean isRunning() {
        return progress.getStatus() != RUNNING;
    }
}