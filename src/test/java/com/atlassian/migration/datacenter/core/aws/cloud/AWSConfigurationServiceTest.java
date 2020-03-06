package com.atlassian.migration.datacenter.core.aws.cloud;

import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AWSConfigurationServiceTest {

    @Mock
    WriteCredentialsService mockCredentialsWriter;

    @Mock
    RegionService mockRegionService;

    @Mock
    MigrationServiceV2 mockMigrationService;

    @InjectMocks
    AWSConfigurationService sut;

    @Test
    void shouldStoreCredentials() throws InvalidMigrationStageError {
        mockValidMigration();

        final String username = "username";
        final String password = "password";
        sut.configureCloudProvider(username, password, "garbage");

        verify(mockCredentialsWriter).storeAccessKeyId(username);
        verify(mockCredentialsWriter).storeSecretAccessKey(password);
    }

    @Test
    void shouldStoreRegion() throws InvalidAWSRegionException, InvalidMigrationStageError {
        mockValidMigration();

        final String region = "region";
        sut.configureCloudProvider("username", "password", region);

        verify(mockRegionService).storeRegion(region);
    }

    @Test
    void shouldStoreCredentialsOnlyWhenStateIsAuthentication() {
        when(mockMigrationService.getCurrentStage()).thenReturn(MigrationStage.FS_MIGRATION_EXPORT);
        assertThrows(InvalidMigrationStageError.class, () -> sut.configureCloudProvider("garbage", "garbage", "garbage"));
    }

    private void mockValidMigration() {
        when(mockMigrationService.getCurrentStage()).thenReturn(MigrationStage.AUTHENTICATION);
    }

    @Test
    void shouldTransitionToProvisionApplicationStageWhenSuccessful() throws InvalidMigrationStageError {
        mockValidMigration();

        sut.configureCloudProvider("garbage", "garbage", "garbage");

        verify(mockMigrationService).transition(MigrationStage.AUTHENTICATION, MigrationStage.PROVISION_APPLICATION);
    }

}