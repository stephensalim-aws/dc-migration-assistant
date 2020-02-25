package com.atlassian.migration.datacenter.core.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class S3Uploader implements Uploader {
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    private static final int SLEEP_TO_WAIT_FOR_CRAWLER = 500; // ms

    private final Map<String, Exception> failedFiles = new HashMap<>();
    private final Queue<S3UploadOperation> responsesQueue = new LinkedList<>();
    private final S3UploadConfig config;

    public S3Uploader(S3UploadConfig config) {
        this.config = config;
    }

    @Override
    public void upload(ConcurrentLinkedQueue<Path> queue, AtomicBoolean isCrawlDone) {
        Path path;
        while (!((path = queue.poll()) == null) || !isCrawlDone.get()) {
            if (path == null) {
                try {
                    Thread.sleep(SLEEP_TO_WAIT_FOR_CRAWLER);
                } catch (InterruptedException e) {
                    logger.error("Interrupted S3 upload, adding all remaining files to failed collection");
                    queue.forEach(p -> addFailedFile(p, e));
                }
            } else {
                if (path == null) {

                }

                if (Files.exists(path)) {
                    String key = config.getSharedHome().relativize(path).toString();
                    final PutObjectRequest putRequest = PutObjectRequest.builder()
                            .bucket(config.getBucketName())
                            .key(key)
                            .build();
                    final CompletableFuture<PutObjectResponse> response = config.getS3AsyncClient().putObject(putRequest, path);
                    final S3UploadOperation uploadOperation = new S3UploadOperation(path, response);

                    responsesQueue.add(uploadOperation);
                } else {
                    addFailedFile(path, new FileNotFoundException(String.format("File doesn't exist: %s", path)));
                }
            }
        }
        responsesQueue.forEach(operation -> {
            try {
                final PutObjectResponse evaluatedResponse = operation.response.get();
                if (!evaluatedResponse.sdkHttpResponse().isSuccessful()) {
                    final String errorMessage = String.format("Error when uploading to S3, %s", evaluatedResponse.sdkHttpResponse().statusText());
                    addFailedFile(operation.path, S3Exception.builder().message(errorMessage).build());
                }
            } catch (InterruptedException | ExecutionException e) {
                addFailedFile(operation.path, e);
            }
        });
    }

    @Override
    public Map<String, Exception> getFailed() {
        return Collections.unmodifiableMap(failedFiles);
    }

    private void addFailedFile(Path path, Exception reason) {
        failedFiles.put(path.toString(), reason);
    }

    private static class S3UploadOperation {
        Path path;
        CompletableFuture<PutObjectResponse> response;

        S3UploadOperation(Path path, CompletableFuture<PutObjectResponse> response) {
            this.path = path;
            this.response = response;
        }
    }
}
