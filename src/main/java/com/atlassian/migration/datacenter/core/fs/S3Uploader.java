package com.atlassian.migration.datacenter.core.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class S3Uploader implements Uploader {
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    private static final int SLEEP_TO_WAIT_FOR_CRAWLER = 500; // ms

    private Map<String, Exception> failedFiles = new HashMap<>();

    @Override
    public void upload(ConcurrentLinkedQueue<Path> queue, AtomicBoolean isCrawlDone) {
        while (!queue.isEmpty() || !isCrawlDone.get()) {
            if (queue.isEmpty()) {
                try {
                    Thread.sleep(SLEEP_TO_WAIT_FOR_CRAWLER);
                } catch (InterruptedException e) {
                    logger.error("Interrupted S3 upload, adding all remaining files to failed collection");
                    queue.forEach(p -> addFailedFile(p, e));
                }
            } else {
                final Path path = queue.poll();
                if (Files.exists(path)) {
                    // TODO upload to S3
                } else {
                    addFailedFile(path, new FileNotFoundException(String.format("File doesn't exist: %s", path)));
                }
            }
        }
    }

    private void addFailedFile(Path path, Exception reason) {
        failedFiles.put(path.toString(), reason);
    }

    @Override
    public Map<String, Exception> getFailed() {
        return Collections.unmodifiableMap(failedFiles);
    }
}
