package com.aws.apn.migration.awsmigrationplugin.core;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage.UNSTARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AWSMigrationServiceTest {

    private AWSMigrationService sut;

    @BeforeEach
    public void initialiseSystemUnderTest() {
        this.sut = new AWSMigrationService();
    }

    @Test
    public void testStageWhenNoMigrationCreatedIsUnstarted() {
        MigrationStage initialStage = sut.getMigrationStage();

        assertEquals(UNSTARTED, initialStage);
    }
}
