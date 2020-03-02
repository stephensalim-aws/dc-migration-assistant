package com.atlassian.migration.datacenter.core.exceptions;

public class InvalidMigrationStageError extends Exception {
    public InvalidMigrationStageError(String message) {
        super(message);
    }
}
