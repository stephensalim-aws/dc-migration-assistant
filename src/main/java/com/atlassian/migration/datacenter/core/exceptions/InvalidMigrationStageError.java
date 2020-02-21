package com.atlassian.migration.datacenter.core.exceptions;

public class InvalidMigrationStageError extends RuntimeException {
    public InvalidMigrationStageError(String message) {
        super(message);
    }
}
