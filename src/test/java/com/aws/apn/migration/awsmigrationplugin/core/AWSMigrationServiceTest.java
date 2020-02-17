package com.aws.apn.migration.awsmigrationplugin.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.aws.apn.migration.awsmigrationplugin.core.aws.CfnApi;
import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InfrastructureProvisioningError;
import com.aws.apn.migration.awsmigrationplugin.core.exceptions.InvalidMigrationStageError;
import com.aws.apn.migration.awsmigrationplugin.dto.Migration;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.infrastructure.ProvisioningConfig;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Optional;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.NOT_STARTED;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.READY_FS_MIGRATION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// We have to use the JUnit 4 API because there is no JUnit 5 active objects extension :(
@RunWith(ActiveObjectsJUnitRunner.class)
public class AWSMigrationServiceTest {

    private ActiveObjects ao;
    private FilesystemMigrationConfig mockConfig;
    private EntityManager entityManager;
    private AWSMigrationService sut;
    private CfnApi cfnApi;
    private FilesystemMigrationService filesystemMigrationService;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        filesystemMigrationService = mock(FilesystemMigrationService.class);
        cfnApi = mock(CfnApi.class);
        sut = new AWSMigrationService(ao, filesystemMigrationService, cfnApi);
    }

    @Test
    public void shouldBeInUnStartedStageWhenNoMigrationExists() {
        ao.migrate(Migration.class);

        MigrationStage initialStage = sut.getMigrationStage();

        assertEquals(NOT_STARTED, initialStage);
    }

    @Test
    public void shouldGetCorrectMigrationStage() {
        initializeAndCreateSingleMigrationWithStage(STARTED);

        assertNumberOfMigrations(1);

        ao.flushAll();

        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(STARTED, currentStage);
    }


    @Test
    public void shouldTransitionStageToStartedWhenMigrationIsStarted() {
        ao.migrate();
        assertNumberOfMigrations(0);

        assertTrue(sut.startMigration());
        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(STARTED, currentStage);
        assertNumberOfMigrations(1);
    }

    @Test
    public void shouldNotStartMigrationWhenExistingStateIsNotUnStarted() {
        initializeAndCreateSingleMigrationWithStage(STARTED);

        ao.flushAll();
        assertNumberOfMigrations(1);

        assertFalse(sut.startMigration());
    }

    @Test
    public void shouldNotStartFileSystemMigrationWhenNoMigrationExists() {
        // given
        ao.migrate(Migration.class);
        // when
        boolean success = sut.startFilesystemMigration(mockConfig);
        // then
        assertFalse(success);
        verify(this.filesystemMigrationService, never()).startMigration(any());
    }

    @Test
    public void shouldStartFsMigrationWhenMigrationStageIsReadyForFsSync() {
        // given
        initializeAndCreateSingleMigrationWithStage(READY_FS_MIGRATION);
        // when
        boolean success = sut.startFilesystemMigration(mockConfig);
        // then
        assertTrue(success);
    }

    @Test
    public void shouldProvisionInfrastructureUsingCloudFormationTemplate() {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();
        String expectedStackId = "arn:stack:test_provision";

        initializeAndCreateSingleMigrationWithStage(MigrationStage.READY_TO_PROVISION);

        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.of(expectedStackId));

        String actualStackId = sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));

        assertEquals(expectedStackId, actualStackId);
    }

    @Test
    public void shouldRaiseAProvisioningErrorWhenStackProvisioningFails() {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();

        initializeAndCreateSingleMigrationWithStage(MigrationStage.READY_TO_PROVISION);
        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.empty());

        assertThrows(InfrastructureProvisioningError.class, () -> {
            sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));
        });
        assertNumberOfMigrations(1);
        assertEquals(MigrationStage.PROVISIONING_ERROR, ao.find(Migration.class)[0].getStage());
    }


    @Test
    public void shouldNotProvisionWhenInitialMigrationStateIsNotReadyToProvision() {
        initializeAndCreateSingleMigrationWithStage(STARTED);

        assertThrows(InvalidMigrationStageError.class, () -> {
            sut.provisionInfrastructure(new ProvisioningConfig("", "", new HashMap<>()));
        });

        verify(this.cfnApi, never()).provisionStack(any(), any(), any());
    }

    private void assertNumberOfMigrations(int i) {
        assertEquals(i, ao.find(Migration.class).length);
    }

    private Migration initializeAndCreateSingleMigrationWithStage(MigrationStage stage) {
        ao.migrate(Migration.class);
        Migration migration = ao.create(Migration.class);
        migration.setStage(stage);
        migration.save();
        return migration;
    }
}
