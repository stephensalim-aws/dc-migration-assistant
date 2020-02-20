package com.atlassian.migration.datacenter.core.aws.auth;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProbeAWSAuth {

    private AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Autowired
    public ProbeAWSAuth(AtlassianPluginAWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Queries the S3 buckets in the AWS account using the AWS Java SDK V1 to test that the credentials have S3 access.
     * @return A list containing the names of the buckets in the account in the current region
     */
    public List<String> probeSDKV1() {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();
        List<Bucket> buckets = s3.listBuckets();
        return buckets
                .stream()
                .map(Bucket::getName)
                .collect(Collectors.toList());
    }

    /**
     * Queries the Cloudformation stacks in the AWS account using the AWS Java SDK V2 to test the credentials
     * have Cloudformation access
     * @return a list containing the names of the stacks in the account in the current region
     */
    public List<String> probeSDKV2() {
        CloudFormationClient client = CloudFormationClient
                .builder()
                .credentialsProvider(credentialsProvider)
                .build();

        DescribeStacksResponse response = client.describeStacks();
        return response
                .stacks()
                .stream()
                .map(Stack::stackName)
                .collect(Collectors.toList());
    }
}
