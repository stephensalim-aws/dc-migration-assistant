package com.atlassian.migration.datacenter.core.aws;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.cloudformation.model.Tag;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CfnApi {

    private final CloudFormationAsyncClient client;

    public CfnApi() {
        this(buildCloudFormationClient());
    }

    public CfnApi(CloudFormationAsyncClient cloudFormationClient) {
        this.client = cloudFormationClient;
    }

    private static CloudFormationAsyncClient buildCloudFormationClient() {
        // FIXME: This should be able to use credentials/region/etc. that are input in the UI.
        return CloudFormationAsyncClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();

    }

    public StackStatus getStatus(String stackName) {
        Optional<Stack> stack = getStack(stackName);
        if (!stack.isPresent()) {
            throw StackInstanceNotFoundException
                    .builder()
                    .message(String.format("Stack with name %s not found", stackName))
                    .build();
        }
        return stack.get().stackStatus();
    }

    public Optional<String> provisionStack(String templateUrl, String stackName, Map<String, String> params) {
        Set<Parameter> parameters = params.entrySet()
                .stream()
                .map(e -> Parameter.builder().parameterKey(e.getKey()).parameterValue(e.getValue()).build())
                .collect(Collectors.toSet());

        Tag tag = Tag.builder()
                .key("created_by")
                .value("atlassian-dcmigration")
                .build();

        CreateStackRequest createStackRequest = CreateStackRequest.builder()
                .templateURL(templateUrl)
                .stackName(stackName)
                .parameters(parameters)
                .tags(tag)
                .build();

        try {
            return Optional.ofNullable(this.client
                    .createStack(createStackRequest)
                    .thenApply(CreateStackResponse::stackId)
                    .get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    public Optional<Stack> getStack(String stackName) {
        DescribeStacksRequest request = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

        CompletableFuture<DescribeStacksResponse> asyncResponse = this.client.describeStacks(request);

        try {
            DescribeStacksResponse response = asyncResponse.join();
            Stack stack = response.stacks().get(0);
            return Optional.ofNullable(stack);
        } catch (CompletionException | CancellationException e) {
            return Optional.empty();
        }
    }
}
