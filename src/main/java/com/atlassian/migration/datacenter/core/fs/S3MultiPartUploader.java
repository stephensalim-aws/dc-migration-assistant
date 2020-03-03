package com.atlassian.migration.datacenter.core.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class S3MultiPartUploader {
    private final static Logger logger = LoggerFactory.getLogger(S3MultiPartUploader.class);

    private final S3UploadConfig config;
    private final List<CompletedPart> completedParts = new ArrayList<>();
    private final int sizeToUpload = 1   * 1024 * 100; // 100MB

    public S3MultiPartUploader(S3UploadConfig config) {
        this.config = config;
    }

    public void multiPartUpload(File file) throws ExecutionException, InterruptedException {
        final String key = file.getName();
        final S3AsyncClient s3AsyncClient = config.getS3AsyncClient();

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(config.getBucketName()).key(key)
                .build();
        CreateMultipartUploadResponse response = s3AsyncClient.createMultipartUpload(createMultipartUploadRequest).get();
        String uploadId = response.uploadId();

        int partNo = 1;

        byte[] buf = new byte[sizeToUpload];
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            while (bufferedInputStream.read(buf) > 0) {
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(config.getBucketName())
                        .key(key)
                        .uploadId(uploadId)
                        .partNumber(1).build();
                String etag = s3AsyncClient.uploadPart(uploadPartRequest, AsyncRequestBody.fromBytes(buf)).get().eTag();
                CompletedPart part = CompletedPart.builder().partNumber(partNo).eTag(etag).build();
                completedParts.add(part);
                logger.info("For file {} uploaded part {}", file.toPath(), partNo);

                partNo++;
            }
        } catch (IOException e) {
            logger.error("Cannot open file for the multi-part upload", e);
        }

        completeUpload(key, uploadId).get();
    }

    private CompletableFuture<CompleteMultipartUploadResponse> completeUpload(String key, String uploadId) {
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().parts(completedParts).build();
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder().bucket(config.getBucketName()).key(key).uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload).build();
        return config.getS3AsyncClient().completeMultipartUpload(completeMultipartUploadRequest);
    }
}
