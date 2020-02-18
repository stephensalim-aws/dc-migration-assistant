package com.atlassian.migration.datacenter.core.aws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test Integration test. No mocks involved
 */
class CfnApiIT {

    public static final String S3_CFN_STACK_URL = "https://dcd-slinghost-templates.s3-ap-southeast-2.amazonaws.com/tests/create_s3_bucket.yaml";
    private CfnApi cfnApi;

    @BeforeEach
    void setUp() {
        cfnApi = new CfnApi();
    }

    @Test
    @Disabled("Disabled until LocalStack is Created")
    public void shouldGetStatusOfExistingCfnStack() {
        String stackArn = "arn:aws:cloudformation:ap-southeast-2:887764444972:stack/jira-analytics-stack-713-001/842750e0-4bbe-11ea-bada-0615bd2a67d8";
        StackStatus status = cfnApi.getStatus(stackArn);
        assertEquals(StackStatus.CREATE_COMPLETE, status);
    }


    @Test
    @Disabled("Disabled until LocalStack is Created")
    public void shouldRaiseExceptionWhenStackDoesNotExist() {
        String stackArn = "arn:aws:cloudformation:ap-southeast-2:1231231231:stack/i-do-not-exist";
        Assertions.assertThrows(StackInstanceNotFoundException.class, () -> {
            cfnApi.getStatus(stackArn);
        });
    }

    @Test
    @Disabled("Disabled until LocalStack is Created")
    public void shouldGetExistingCfnStack() {
        String stackArn = "arn:aws:cloudformation:ap-southeast-2:887764444972:stack/jira-analytics-stack-713-001/842750e0-4bbe-11ea-bada-0615bd2a67d8";
        Optional<Stack> stack = cfnApi.getStack(stackArn);
        assertEquals(StackStatus.CREATE_COMPLETE, stack.get().stackStatus());
    }

    @Test
    @Disabled("Disabled until LocalStack is Created")
        //TODO: Delete stack when complete after each test
    void shouldProvisionNewCfnStack() {
        String random = UUID
                .randomUUID().toString().split("-")[0];
        String stackName = String.format("mothra-test-%s", random);
        Optional<String> provisionedStackId = cfnApi.provisionStack(S3_CFN_STACK_URL, stackName, new HashMap<String, String>() {{
            put("S3BucketName", stackName + "-bucket");
        }});
        assertFalse(stackName.isEmpty());
        assertNotNull(provisionedStackId.get());

        assertEquals(StackStatus.CREATE_COMPLETE, cfnApi.getStatus(stackName));
    }
}