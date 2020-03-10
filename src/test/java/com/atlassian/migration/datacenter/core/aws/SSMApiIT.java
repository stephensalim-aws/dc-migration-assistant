package com.atlassian.migration.datacenter.core.aws;

import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationRequest;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;

import java.net.URI;
import java.util.HashMap;

@Tag("integration")
@ExtendWith({LocalstackDockerExtension.class})
@LocalstackDockerProperties(services = {"cloudformation", "s3", "ssm"}, imageTag = "0.10.8")
public class SSMApiIT {

    private static final String LOCALSTACK_SSM_ENDPOINT = "http://localhost:4583";

    @Test
    @Disabled("Localstack is giving 500s for SSM send-command")
    void shouldStartSSMCommand() {
        SsmClient client = SsmClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_SSM_ENDPOINT))
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return TestUtils.TEST_ACCESS_KEY;
                    }

                    @Override
                    public String secretAccessKey() {
                        return TestUtils.TEST_SECRET_KEY;
                    }
                })
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .build();

        SendCommandRequest request = SendCommandRequest.builder()
                .documentName("document")
                .documentVersion("$LATEST")
                .timeoutSeconds(600)
                .comment("running command to pull files down as part of migration")
                .parameters(new HashMap<>())
                .instanceIds("i-1234567")
                .outputS3BucketName("migration-bucket")
                .outputS3KeyPrefix("fs-copy-down-log")
                .build();
        client.sendCommand(request);
    }

    @Test
    @Disabled("Localstack is giving 400s for SSM getCommandInvocation")
    void shouldGetStatusOfRunningCommand() {
        SsmClient client = SsmClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_SSM_ENDPOINT))
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return TestUtils.TEST_ACCESS_KEY;
                    }

                    @Override
                    public String secretAccessKey() {
                        return TestUtils.TEST_SECRET_KEY;
                    }
                })
                .region(Region.of(TestUtils.DEFAULT_REGION))
                .build();

        GetCommandInvocationRequest request = GetCommandInvocationRequest.builder()
                .commandId("commandID")
                .instanceId("theInstance")
                .build();

        GetCommandInvocationResponse response = client.getCommandInvocation(request);

        System.out.println(response.standardOutputContent());
    }

}