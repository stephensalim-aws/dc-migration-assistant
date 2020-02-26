package com.atlassian.migration.datacenter.spi.fs;

import java.util.Map;

public interface FailableFileOperation {
    FailedFileMigrationReport getFailed();
}
