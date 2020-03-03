package com.atlassian.migration.datacenter.core.fs;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class LargeFileUploadIT {

    private long putLimitInBytes = 5_368_709_120L;

    @Test
    void testLargeFilePutObject() throws ExecutionException, InterruptedException {
        // 5 GB
        Path p = Paths.get("/Users/abrokes/Downloads/2020-01-07T01-15-40Z-shared-home-backup.tar.gz");
        final File file = p.toFile();

        assertTrue(file.exists(), "expecting file to exists");
        assertThat(file.length(), is(greaterThan(putLimitInBytes)));

        S3AsyncClient asyncClient = S3AsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                "AKIA45MXCM4WFZZJ2PPL",
                                "WRDI8s/IvAyRlxLTLcI76NgJF14q11yvylfKr5rI"
                        )))
                .build();

        final String bucketName = "trebuchet-abrokes";
        final PutObjectRequest request = PutObjectRequest.builder().key("test-more-5-gigs").bucket(bucketName).build();
        final CompletableFuture<PutObjectResponse> putObjectResponseCompletableFuture = asyncClient.putObject(request, p);
        try {
            final PutObjectResponse response = putObjectResponseCompletableFuture.get();
            System.out.println(response.sdkHttpResponse().statusText().orElse("missing status text"));

        } catch (Exception e) {
            if (e.getCause() instanceof S3Exception) {

                S3Exception s3Exception = (S3Exception) e.getCause();
                if (s3Exception.awsErrorDetails().errorCode().equals("EntityTooLarge")) {
                    System.out.println("Exceeeds max size");
                    multiPartUpload(asyncClient, file, bucketName);
                }
            }
        }
    }

    private void multiPartUpload(S3AsyncClient asyncClient, File file, String bucketName) throws ExecutionException, InterruptedException {
        final int sizeToUpload = 1024 * 1024 * 100; // 100MB
        final String key = file.getName();
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName).key(key)
                .build();
        CreateMultipartUploadResponse response = asyncClient.createMultipartUpload(createMultipartUploadRequest).get();
        String uploadId = response.uploadId();

        int partNo = 1;
        List<CompletedPart> completedParts = new ArrayList<>();


        byte[] buf = new byte[sizeToUpload];
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            int bytesRead = 0;
            while ((bytesRead = bufferedInputStream.read(buf)) > 0) {
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .partNumber(1).build();
                String etag = asyncClient.uploadPart(uploadPartRequest, AsyncRequestBody.fromBytes(buf)).get().eTag();
                CompletedPart part = CompletedPart.builder().partNumber(partNo).eTag(etag).build();
                completedParts.add(part);
                System.out.println("Processed part " + partNo);

                partNo++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().parts(completedParts).build();
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder().bucket(bucketName).key(key).uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload).build();
        asyncClient.completeMultipartUpload(completeMultipartUploadRequest).get();
    }
}
