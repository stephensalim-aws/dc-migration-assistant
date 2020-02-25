package com.atlassian.migration.datacenter.core.fs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Disabled("Exploratory test")
public class S3UploadTest {

    public static final String FOLDER = "/Users/abrokes/code/taskcat";
    final Queue<CompletableFuture<PutObjectResponse>> responseQueue = new LinkedList<>();

    @Test
    void listAllFilesInDirectory() throws IOException, ExecutionException, InterruptedException {
        ConcurrentLinkedQueue<Path> queue = new ConcurrentLinkedQueue<>();
        final AtomicBoolean isCrawled = new AtomicBoolean(false);

        final int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores " + Runtime.getRuntime().availableProcessors());
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        final Future<?> s3upload = executorService.submit(() -> {
            while (!isCrawled.get() || !queue.isEmpty()) {
                if (queue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final Path p = queue.poll();
                Region region = Region.US_EAST_1;
                final S3AsyncClient asyncClient = S3AsyncClient.builder().region(region).build();

                String bucket = "slingshot-2-test";

                String key = Paths.get(FOLDER).relativize(p).toString();
                System.out.println(key);
                final CompletableFuture<PutObjectResponse> response = asyncClient.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), p);
                responseQueue.add(response);
            }
        });

        final DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(FOLDER));
        listDirectories(queue, paths);
        isCrawled.set(true);
        System.out.println(queue.size());
        s3upload.get();
        responseQueue.forEach(r -> {
            try {
                r.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private void listDirectories(ConcurrentLinkedQueue<Path> queue, DirectoryStream<Path> paths) {
        paths.forEach(p -> {
            if (Files.isDirectory(p)) {
                try (final DirectoryStream<Path> newPaths = Files.newDirectoryStream(p.toAbsolutePath())) {
                    listDirectories(queue, newPaths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                queue.add(p);
            }
        });
    }

}
