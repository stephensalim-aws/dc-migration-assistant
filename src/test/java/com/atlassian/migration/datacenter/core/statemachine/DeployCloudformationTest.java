package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.statemachine.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        final String stackName = "stack name";
        final String templateUrl = "s3://bucket/template.yml";

        when(mockMigrationContext.getAppStackName()).thenReturn(stackName);
        when(mockMigrationContext.getAppTemplateURL()).thenReturn(templateUrl);
        when(mockMigrationContext.getAppTemplateParameters()).thenReturn(params);

        sut.run();

        verify(mockCfnApi).provisionStack(templateUrl, stackName, params);
    }

}