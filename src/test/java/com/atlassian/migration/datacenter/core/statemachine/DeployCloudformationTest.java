package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.statemachine.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.amazon.awssdk.services.cloudformation.model.StackStatus.CREATE_IN_PROGRESS;

@ExtendWith(MockitoExtension.class)
class DeployCloudformationTest {

    private DeployCloudformation sut;

    @Mock
    CfnApi mockCfnApi;

    @Mock
    AWSMigrationContext mockMigrationContext;

    @BeforeEach
    void setUp() {
        sut = new DeployCloudformation(mockMigrationContext, mockCfnApi);
    }

    @Test
    void shouldTransitionToCloudformationWaiting() {
        State nextState = sut.nextState();

        assertEquals(WaitCloudformation.class, nextState.getClass());
    }

    @Test
    void shouldInitiateDeploymentWhenRun() {
        final Map<String, String> params = new HashMap<>();
        params.put("Parameter1", "value");
        final String stackName = "stack-name";
        final String templateUrl = "s3://bucket/template.yml";

        when(mockMigrationContext.getAppStackName()).thenReturn(stackName);
        when(mockMigrationContext.getAppTemplateURL()).thenReturn(templateUrl);
        when(mockMigrationContext.getAppTemplateParameters()).thenReturn(params);

        sut.run();

        verify(mockCfnApi).provisionStack(templateUrl, stackName, params);
    }

    @ParameterizedTest
    @EnumSource(value = StackStatus.class, names = {"CREATE_COMPLETE", "CREATE_FAILED"})
    void shouldIndicateReadyToTransitionWhenStackHasFinishedDeploying(StackStatus status) {
        final String stackName = "stack-name";
        when(mockMigrationContext.getAppStackName()).thenReturn(stackName);
        when(mockCfnApi.getStatus(stackName)).thenReturn(status);

        assertTrue(sut.readyToTransition());
    }

    @Test
    void shouldNotIndicateReadyToTransitionIfStackStillDeploying() {
        final String stackName = "stack-name";
        when(mockMigrationContext.getAppStackName()).thenReturn(stackName);

        when(mockCfnApi.getStatus(stackName)).thenReturn(CREATE_IN_PROGRESS);

        assertFalse(sut.readyToTransition());
    }

    @Test
    void shouldNotIndicateReadyToTransitionIfNoStackExists() {
        final String stackName = "stack-name";
        when(mockMigrationContext.getAppStackName()).thenReturn(stackName);

        when(mockCfnApi.getStatus(stackName)).thenThrow(StackInstanceNotFoundException.builder().message("it ain't here fam").build());

        assertFalse(sut.readyToTransition());
    }
}