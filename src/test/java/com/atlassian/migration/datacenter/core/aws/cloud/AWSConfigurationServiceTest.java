package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AWSConfigurationServiceTest {

    @Mock
    WriteCredentialsService mockCredentialsWriter;

    @Mock
    RegionService mockRegionService;

    @InjectMocks
    AWSConfigurationService sut;
    
    @Test
    void shouldStoreCredentials() {
        final String username = "username";
        final String password = "password";
        sut.configureCloudProvider(username, password, "garbage");

        verify(mockCredentialsWriter).storeAccessKeyId(username);
        verify(mockCredentialsWriter).storeSecretAccessKey(password);
    }

}