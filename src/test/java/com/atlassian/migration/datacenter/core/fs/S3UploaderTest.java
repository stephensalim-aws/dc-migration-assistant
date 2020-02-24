package com.atlassian.migration.datacenter.core.fs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3UploaderTest {
    @TempDir
    Path tempDir;
    private ConcurrentLinkedQueue<Path> queue;
    private Uploader uploader;
    private AtomicBoolean isCrawlDone;

    @BeforeEach
    void setup() {
        queue = new ConcurrentLinkedQueue<>();
        uploader = new S3Uploader();
        isCrawlDone = new AtomicBoolean(false);
    }

    @Test
    void uploadShouldConsumePathsWhileCrawlingIsRunning() throws IOException, InterruptedException, ExecutionException {
        final Path file = tempDir.resolve("file");
        Files.write(file, "".getBytes());
        queue.add(file);

        final Future<?> submit = Executors.newFixedThreadPool(1).submit(() -> {
            uploader.upload(queue, isCrawlDone);
        });

        // verify consumption of the first path
        Thread.sleep(1000);
        assertFalse(submit.isDone());
        assertTrue(queue.isEmpty());

        // add new file when the crawler isn't finish
        final Path file2 = tempDir.resolve("file");
        Files.write(file2, "".getBytes());
        queue.add(file2);

        // finish crawling
        isCrawlDone.set(true);
        submit.get();

        // upload should finish and there shouldn't be more paths to process
        assertTrue(submit.isDone());
        assertTrue(queue.isEmpty());
        assertTrue(uploader.getFailed().isEmpty());
    }

    @Test
    void uploadNonExistentDirectoryShouldReturnFailedCollection() {
        final Path nonExistentFile = tempDir.resolve("non-existent");
        isCrawlDone.set(true);
        queue.add(nonExistentFile);

        uploader.upload(queue, isCrawlDone);

        assertEquals(uploader.getFailed().size(), 1);
    }
}
