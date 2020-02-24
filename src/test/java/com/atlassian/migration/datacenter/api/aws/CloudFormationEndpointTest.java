package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudFormationEndpointTest {
    @Mock
    private MigrationService migrationService;

    @InjectMocks
    private CloudFormationEndpoint endpoint;

    @Test
    public void shouldAcceptRequestToProvisionCloudFormationStack() {
        ProvisioningConfig provisioningConfig = new ProvisioningConfig("url", "stack-name", new HashMap<>());

        String stackId = "stack-id";
        when(this.migrationService.provisionInfrastructure(provisioningConfig)).thenReturn(stackId);
        Response response = endpoint.provisionInfrastructure(provisioningConfig);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertEquals(stackId, response.getEntity());
    }

    @Test
    public void shouldBeBadRequestWhenCurrentMigrationStageIsNotValid() {
        ProvisioningConfig provisioningConfig = new ProvisioningConfig("url", "stack-name", new HashMap<>());

        when(this.migrationService.provisionInfrastructure(provisioningConfig)).thenThrow(new InvalidMigrationStageError("error"));

        Response response = endpoint.provisionInfrastructure(provisioningConfig);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldGetCurrentProvisioningStatusForGivenStackId() {
        String stackId = "stackIdentifier";
        String provisioningStatus = StackStatus.CREATE_IN_PROGRESS.toString();

        when(this.migrationService.getInfrastructureProvisioningStatus(stackId)).thenReturn(Optional.of(provisioningStatus));
        Response response = endpoint.getInfrastructureStatus(stackId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(provisioningStatus, response.getEntity());
    }
}