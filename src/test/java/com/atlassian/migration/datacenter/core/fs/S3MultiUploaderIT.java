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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith({LocalstackDockerExtension.class, MockitoExtension.class})
@LocalstackDockerProperties(services = {"s3"}, imageTag = "0.10.8")
class S3MultiUploaderIT {
    private static final String LOCALSTACK_S3_ENDPOINT = "http://localhost:4572";
    private static final String TREBUCHET_LOCALSTACK_BUCKET = "trebuchet-localstack-bucket";
    private S3MultiPartUploader uploader;

    @Mock
    private AwsCredentialsProvider mockCredentialsProvider;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        S3AsyncClient localStackS3Client = S3AsyncClient.builder()
                .credentialsProvider(mockCredentialsProvider)
                .endpointOverride(URI.create(LOCALSTACK_S3_ENDPOINT))
                .region(Region.US_EAST_1)
                .build();
        S3UploadConfig config = new S3UploadConfig(TREBUCHET_LOCALSTACK_BUCKET, localStackS3Client, tempDir);

        when(mockCredentialsProvider.resolveCredentials()).thenReturn(new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return "fake-access-key";
            }

            @Override
            public String secretAccessKey() {
                return "fake-secret-key";
            }
        });

        uploader = new S3MultiPartUploader(config);
    }

    @Test
    void uploadShouldUploadPathsFromQueueToS3() throws Exception {
        final String filename = "file_to_upload.txt";
        // we use subfolder to verify we are writing the correct key
        final Path file = createFile("subfolder", filename);
        final String key = tempDir.relativize(file).toString();
        Files.write(file, "content".getBytes());

        AmazonS3 s3Client = TestUtils.getClientS3();
        s3Client.createBucket(TREBUCHET_LOCALSTACK_BUCKET);

        uploader.multiPartUpload(file.toFile(), key);

        final List<S3ObjectSummary> objectSummaries = s3Client.listObjects(TREBUCHET_LOCALSTACK_BUCKET).getObjectSummaries();
        assertEquals(1, objectSummaries.size(), "Bucket should contain only one file");
        assertEquals(key, objectSummaries.get(0).getKey(), "Object key is different to the filename");
    }

    Path createFile(String subfolder, String fileName) throws IOException {
        Files.createDirectory(tempDir.resolve(subfolder));
        final Path file = tempDir.resolve(subfolder).resolve(fileName);
        Files.write(file, "".getBytes());
        return file;
    }
}
