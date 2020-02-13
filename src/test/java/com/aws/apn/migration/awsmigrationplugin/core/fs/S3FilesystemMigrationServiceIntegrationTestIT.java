package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationConfig;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationProgress;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationStatus;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "integration", matches = "true")
class S3FilesystemMigrationServiceIntegrationTestIT {
    @TempDir
    Path dir;

    String s3Bucket = "slingshot-2-test";

    @BeforeEach
    void setup() throws Exception {
        Path file = dir.resolve(UUID.randomUUID().toString());
        String rand = String.format("Testing string %s", Instant.now());
        Files.write(file, Collections.singleton(rand));
    }

    @Test
    void testSuccessfulDirectoryMigration(@TempDir Path dir) {
        System.out.println(System.getenv("AWS_ACCESS_KEY_ID"));
        FilesystemMigrationConfig config = new FilesystemMigrationConfig(s3Bucket, dir);
        FilesystemMigrationService fsService = new S3FilesystemMigrationService();
        fsService.startMigration(config);
        Assertions.assertNotEquals(FilesystemMigrationStatus.FAILED, fsService.getProgress().getStatus());
    }
}
