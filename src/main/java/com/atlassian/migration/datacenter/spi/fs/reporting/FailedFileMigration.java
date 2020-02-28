package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.nio.file.Path;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonSerialize
public class FailedFileMigration {

    private Path filePath;

    private String reason;

    public FailedFileMigration(Path filePath, String reason) {
        this.filePath = filePath;
        this.reason = reason;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getReason() {
        return reason;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
