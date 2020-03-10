package com.atlassian.migration.datacenter.core.aws;

import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

import java.util.List;
import java.util.Map;

public class SSMApi {

    private final AwsCredentialsProvider credentialsService;
    private final RegionService regionService;

    // This may be null, use getClient() to safely get a non-null SSM client
    private SsmClient client;

    public SSMApi(AwsCredentialsProvider credentialsService, RegionService regionService) {
        this.credentialsService = credentialsService;
        this.regionService = regionService;
    }

    /**
     * Runs an SSM automatiom document against a specific EC2 instance with the specified parameters.
     * @param documentName The name of the document to run. The latest version will be used.
     * @param targetEc2InstanceId The instance ID of the EC2 instance to run this command on
     * @param commandParameters
     * @return the command ID of the invoked command.
     */
    public String runSSMDocument(String documentName, String targetEc2InstanceId, Map<String, List<String>> commandParameters) {
        SendCommandRequest request = SendCommandRequest.builder()
                .documentName(documentName)
                .documentVersion("$LATEST")
                .instanceIds(targetEc2InstanceId)
                .parameters(commandParameters)
                .timeoutSeconds(600)
                .comment("command run by Jira DC Migration Assistant")
                .outputS3BucketName("migration-bucket")
                .outputS3KeyPrefix("fs-copy-down-log")
                .build();

        SendCommandResponse response = getClient().sendCommand(request);

        return response.command().commandId();
    }

    // Lazily instantiates the ssm client
    private SsmClient getClient() {
        if (client == null) {
            client = SsmClient.builder()
                    .credentialsProvider(credentialsService)
                    .region(Region.of(regionService.getRegion()))
                    .build();
        }

        return client;
    }
}
