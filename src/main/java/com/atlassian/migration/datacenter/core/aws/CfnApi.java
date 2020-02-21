package com.atlassian.migration.datacenter.core.aws;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class CfnApi {

    private static Logger LOGGER = Logger.getLogger(CfnApi.class);

    private final CloudFormationAsyncClient client;

    public CfnApi() {
        this(buildCloudFormationClient());
    }

    public CfnApi(CloudFormationAsyncClient cloudFormationClient) {
        this.client = cloudFormationClient;
    }

    private static CloudFormationAsyncClient buildCloudFormationClient() {
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
                .parallelStream()
                .map(e -> Parameter.builder().parameterKey(e.getKey()).parameterValue(e.getValue()).build())
                .collect(Collectors.toSet());

        return this.provisionStack(templateUrl, stackName, parameters);
    }

    public final Optional<String> provisionStack(final String templateUrl, final String stackName, final Collection<Parameter> parameters) {
        Tag tag = Tag.builder()
                .key("created_by")
                .value("atlassian-awsmigration-plugin")
                .build();

        CreateStackRequest createStackRequest = CreateStackRequest.builder()
                .templateURL(templateUrl)
                .stackName(stackName)
                .parameters(parameters)
                .tags(tag)
                .build();

        try {
            return Optional.ofNullable(this.client.createStack(createStackRequest)
                    .thenComposeAsync(this::awaitStackCreation)
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
            LOGGER.error(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private CompletableFuture<String> awaitStackCreation(CreateStackResponse createStackResponse) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String stackId = createStackResponse.stackId();
            StackStatus status = this.getStatus(stackId);
            if (Objects.equals(status, StackStatus.CREATE_COMPLETE)) {
                completableFuture.complete(stackId);
            }
        }, 0, 10, TimeUnit.SECONDS);

        completableFuture.whenComplete((result, thrown) -> {
            scheduledFuture.cancel(true);
        });
        return completableFuture;
    }
}
