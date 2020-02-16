package com.aws.apn.migration.awsmigrationplugin.core.exceptions;

public class InvalidMigrationStageError extends RuntimeException {
    public InvalidMigrationStageError(String message) {
        super(message);
    }
}
