package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.cloud.AWSConfigurationService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AWSConfigureEndpointTest {

    @Mock
    AWSConfigurationService configurationService;

    @InjectMocks
    AWSConfigureEndpoint sut;

    @Test
    void shouldConfigureAWS() throws InvalidMigrationStageError {
        AWSConfigureEndpoint.AWSConfigureWebObject payload = new AWSConfigureEndpoint.AWSConfigureWebObject();
        final String accessKeyId = "accessKeyId";
        final String secretKey = "secretKey";
        final String region = "us-east-1";
        payload.setAccessKeyId(accessKeyId);
        payload.setSecretAccessKey(secretKey);
        payload.setRegion(region);

        sut.storeAWSCredentials(payload);

        verify(configurationService).configureCloudProvider(accessKeyId, secretKey, region);
    }

}