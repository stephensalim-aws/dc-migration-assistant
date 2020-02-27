package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.migration.datacenter.spi.fs.reporting.FilesystemMigrationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3FilesystemMigrationServiceTest {
    @Mock
    RegionService regionService;

    @Mock
    AtlassianPluginAWSCredentialsProvider credentialsProvider;

    @Mock
    JiraHome jiraHome;

    @Test
    void migrationShouldFailWithIncorrectDirectory() {
        // given
        Path nonexistentDir = Paths.get(UUID.randomUUID().toString());
        when(jiraHome.getHome()).thenReturn(nonexistentDir.toFile());

        when(regionService.getRegion()).thenReturn(Region.US_EAST_1.toString());
        S3FilesystemMigrationService fsService = new S3FilesystemMigrationService(regionService, credentialsProvider, jiraHome);
        // when
        fsService.startMigration();
        // then
        assertEquals(FilesystemMigrationStatus.FAILED, fsService.getReport().getStatus());
    }
}
