package com.aws.apn.migration.awsmigrationplugin.core;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationStatus;
import com.aws.apn.migration.awsmigrationplugin.dto.Migration;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.NOT_STARTED;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.READY_FS_MIGRATION;
import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// We have to use the JUnit 4 API because there is no JUnit 5 active objects extension :(
@RunWith(ActiveObjectsJUnitRunner.class)
public class AWSMigrationServiceTest {

    private ActiveObjects ao;

    private FilesystemMigrationConfig mockConfig;

    private EntityManager entityManager;

    private AWSMigrationService sut;

    @Before
    public void setup() {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        sut = new AWSMigrationService(ao);
    }

    @Test
    public void testStageWhenNoMigrationCreatedIsUnstarted() {
        ao.migrate(Migration.class);

        MigrationStage initialStage = sut.getMigrationStage();

        assertEquals(NOT_STARTED, initialStage);
    }

    @Test
    public void testStageOfCurrentMigrationIsReturned() {
        ao.migrate(Migration.class);

        createMigration(STARTED);

        assertNumberOfMigrations(1);

        ao.flushAll();

        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(STARTED, currentStage);
    }


    @Test
    public void testStageIsStartedAfterStartingMigration() {
        ao.migrate();
        assertNumberOfMigrations(0);

        assertTrue(sut.startMigration());
        MigrationStage currentStage = sut.getMigrationStage();

        assertEquals(STARTED, currentStage);
        assertNumberOfMigrations(1);
    }

    @Test
    public void testStageIsUnstartedWhenNoMigrationExists() {
        ao.migrate(Migration.class);
        createMigration(STARTED);

        ao.flushAll();
        assertNumberOfMigrations(1);

        assertFalse(sut.startMigration());
    }

    @Test
    public void testFsMigrationFailsWhenNotReady() {
        // given
        ao.migrate(Migration.class);
        FilesystemMigrationService fsService = mock(FilesystemMigrationService.class);
        // when
        FilesystemMigrationProgress progress = sut.startFilesystemMigration(fsService, mockConfig);
        // then
        assertEquals(FilesystemMigrationStatus.FAILED, progress.getStatus());
    }

    @Test
    public void testFsMigrationRunningWhenReadyForFsSync() {
        // given
        ao.migrate(Migration.class);
        createMigration(READY_FS_MIGRATION);
        FilesystemMigrationService fsService = mock(FilesystemMigrationService.class);
        when(fsService.startMigration(mockConfig)).thenReturn(new FilesystemMigrationProgress(FilesystemMigrationStatus.RUNNING));
        // when
        FilesystemMigrationProgress progress = sut.startFilesystemMigration(fsService, mockConfig);
        // then
        assertEquals(FilesystemMigrationStatus.RUNNING, progress.getStatus());
    }

    private void assertNumberOfMigrations(int i) {
        assertEquals(i, ao.find(Migration.class).length);
    }

    private void createMigration(MigrationStage stage) {
        Migration migration = ao.create(Migration.class);
        migration.setStage(stage);
        migration.save();
    }
}
