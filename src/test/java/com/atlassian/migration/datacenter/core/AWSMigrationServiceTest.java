package com.atlassian.migration.datacenter.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.aws.CfnApiFactory;
import com.atlassian.migration.datacenter.core.exceptions.InfrastructureProvisioningError;
import com.atlassian.migration.datacenter.core.exceptions.InvalidMigrationStageError;
import com.atlassian.migration.datacenter.dto.Migration;
import com.atlassian.migration.datacenter.spi.MigrationStage;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.infrastructure.ProvisioningConfig;
import com.atlassian.scheduler.SchedulerService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;
import java.util.Optional;

import static com.atlassian.migration.datacenter.spi.MigrationStage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// We have to use the JUnit 4 API because there is no JUnit 5 active objects extension :(
@RunWith(ActiveObjectsJUnitRunner.class)
public class AWSMigrationServiceTest {

    private ActiveObjects ao;
    private EntityManager entityManager;
    private AWSMigrationService sut;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private CfnApi cfnApi;
    @Mock private CfnApiFactory cfnApiFactory;
    @Mock private FilesystemMigrationService filesystemMigrationService;
    @Mock private SchedulerService schedulerService;

    @Before
    public void setup() {
        assertNotNull(entityManager);

        ao = new TestActiveObjects(entityManager);

        when(cfnApiFactory.getCfnApi()).thenReturn(cfnApi);

        sut = new AWSMigrationService(ao, filesystemMigrationService, cfnApiFactory, schedulerService);
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
        boolean success = sut.startFilesystemMigration();
        // then
        assertFalse(success);
        verify(this.filesystemMigrationService, never()).startMigration();
    }

    @Test
    public void shouldStartFsMigrationWhenMigrationStageIsReadyForFsSync() {
        // given
        initializeAndCreateSingleMigrationWithStage(READY_FS_MIGRATION);
        // when
        boolean success = sut.startFilesystemMigration();
        // then
        assertTrue(success);
    }

    @Test
    public void shouldProvisionInfrastructureUsingCloudFormationTemplate() {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();
        String expectedStackId = "arn:stack:test_provision";

        initializeAndCreateSingleMigrationWithStage(STARTED);

        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.of(expectedStackId));

        String actualStackId = sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));

        assertEquals(expectedStackId, actualStackId);
    }

    @Test
    public void shouldRaiseAProvisioningErrorWhenStackProvisioningFails() {
        String templateUrl = "https://template.url", stackName = "test_provision";
        HashMap<String, String> params = new HashMap<>();

        initializeAndCreateSingleMigrationWithStage(STARTED);
        when(this.cfnApi.provisionStack(templateUrl, stackName, params)).thenReturn(Optional.empty());

        assertThrows(InfrastructureProvisioningError.class, () -> {
            sut.provisionInfrastructure(new ProvisioningConfig(templateUrl, stackName, params));
        });
        assertNumberOfMigrations(1);
        assertEquals(MigrationStage.PROVISIONING_ERROR, ao.find(Migration.class)[0].getStage());
    }


    @Test
    public void shouldNotProvisionWhenInitialMigrationStateIsNotReadyToProvision() {
        initializeAndCreateSingleMigrationWithStage(NOT_STARTED);

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
