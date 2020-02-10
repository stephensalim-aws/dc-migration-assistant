package com.aws.apn.migration.awsmigrationplugin.api;

import com.aws.apn.migration.awsmigrationplugin.spi.MigrationService;
import com.aws.apn.migration.awsmigrationplugin.spi.MigrationStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestMigrationEndpoint {

    @Mock
    private MigrationService mockMigrationService;

    @InjectMocks
    private MigrationEndpoint sut;

    @Test
    public void testOKAndMigrationStatusWhenMigrationExists() {
        when(mockMigrationService.getMigrationStage()).thenReturn(MigrationStage.STARTED);

        Response response = sut.getMigrationStatus();

        assertEquals("started", response.getEntity());
    }

    @Test
    public void testNotFoundWhenMigrationDoesNotExist() {
        when(mockMigrationService.getMigrationStage()).thenReturn(MigrationStage.UNSTARTED);

        Response response = sut.getMigrationStatus();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testNoContentWhenCreatingMigrationAndNoneExists() {
        when(mockMigrationService.startMigration()).thenReturn(true);

        Response response = sut.createMigration();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBadRequestWhenCreatingMigrationAndOneExists() {
        when(mockMigrationService.startMigration()).thenReturn(false);

        Response response = sut.createMigration();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

}
