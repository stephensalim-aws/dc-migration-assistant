package com.atlassian.migration.datacenter.core.aws.auth;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.atlassian.migration.datacenter.core.aws.region.RegionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class ProbeAWSAuth {
    private static final Logger logger = LoggerFactory.getLogger(ProbeAWSAuth.class);

    private AtlassianPluginAWSCredentialsProvider credentialsProvider;
    private RegionManagement regionManagement;

    @Autowired
    public ProbeAWSAuth(AtlassianPluginAWSCredentialsProvider credentialsProvider, RegionManagement regionManagement) {
        this.credentialsProvider = credentialsProvider;
        this.regionManagement = regionManagement;
    }

    /**
     * Queries the S3 buckets in the AWS account using the AWS Java SDK V1 to test that the credentials have S3 access.
     *
     * @return A list containing the names of the buckets in the account in the current region
     */
    public List<String> probeSDKV1() {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(regionManagement.getRegion())
                .withCredentials(credentialsProvider).build();
        List<Bucket> buckets = s3.listBuckets();
        return buckets
                .stream()
                .map(Bucket::getName)
                .collect(Collectors.toList());
    }

    /**
     * Queries the Cloudformation stacks in the AWS account using the AWS Java SDK V2 to test the credentials
     * have Cloudformation access
     *
     * @return a list containing the names of the stacks in the account in the current region
     */
    public List<String> probeSDKV2() {
        CloudFormationAsyncClient client = CloudFormationAsyncClient
                .builder()
                .region(Region.of(regionManagement.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();

        CompletableFuture<DescribeStacksResponse> futureResponse = client.describeStacks();

        try {
            DescribeStacksResponse response = futureResponse.get();
            return response
                    .stacks()
                    .stream()
                    .map(Stack::stackName)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("unable to get DescribeStacksResponse", e);
            return Collections.emptyList();
        }
    }
}
