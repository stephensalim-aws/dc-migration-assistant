package com.atlassian.migration.datacenter.core.fs;

import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationStatus;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class S3UploadMonitorRunner implements JobRunner {
    public static String KEY = "com.atlassian.migration.datacenter.fs.S3UploadMonitorRunner";
    private static Logger log = LoggerFactory.getLogger(S3UploadMonitorRunner.class);
    private final FilesystemMigrationService s3Service;

    private MultipleFileUpload upload;

    public S3UploadMonitorRunner(FilesystemMigrationService fsMigrationService) {
        this.s3Service = fsMigrationService;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        if (s3Service.getProgress().getStatus() == FilesystemMigrationStatus.RUNNING) {
            log.debug("Transferred {} bytes", s3Service.getProgress().getBytesTransferred());
            return JobRunnerResponse.success();
        }
        return JobRunnerResponse.success();
    }

}
