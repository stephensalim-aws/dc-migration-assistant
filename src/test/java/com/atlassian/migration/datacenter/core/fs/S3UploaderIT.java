package com.atlassian.migration.datacenter.core.fs;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = {"s3"})
class S3UploaderIT {
    static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4572";
    static final String TREBUCHET_LOCALSTACK_BUCKET = "trebuchet-localstack-bucket";
    ConcurrentLinkedQueue<Path> queue = new ConcurrentLinkedQueue<>();
    S3Uploader uploader;
    AtomicBoolean isCrawlDone;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        S3AsyncClient localStackS3Client = S3AsyncClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_S3_ENDPOINT))
                .region(Region.US_EAST_1)
                .build();
        S3UploadConfig config = new S3UploadConfig(TREBUCHET_LOCALSTACK_BUCKET, localStackS3Client, tempDir);

        isCrawlDone = new AtomicBoolean(false);
        queue = new ConcurrentLinkedQueue<>();
        uploader = new S3Uploader(config);
    }

    @Test
    void uploadShouldUploadPathsFromQueueToS3() throws IOException {
        final Path file = addFileToQueue("file");
        isCrawlDone.set(true);

        AmazonS3 s3Client = TestUtils.getClientS3();
        s3Client.createBucket(TREBUCHET_LOCALSTACK_BUCKET);

        uploader.upload(queue, isCrawlDone);

        assertTrue(queue.isEmpty());

        final List<S3ObjectSummary> objectSummaries = s3Client.listObjects(TREBUCHET_LOCALSTACK_BUCKET).getObjectSummaries();

        assertEquals(objectSummaries.size(), 1);
        assertEquals(objectSummaries.get(0).getKey(), tempDir.relativize(file).toString());
    }

    Path addFileToQueue(String fileName) throws IOException {
        final Path file = tempDir.resolve(fileName);
        Files.write(file, "".getBytes());
        queue.add(file);
        return file;
    }
}
