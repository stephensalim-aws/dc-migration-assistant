package com.atlassian.migration.datacenter.core.fs;

import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationConfig;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

/**
 * Scheduled job starting directory upload to S3 bucket. If there is already job running, it will abort execution.
 * The job expects 2 parameters being passed when the job is scheduled
 * <ul>
 * <li>s3bucket: target S3 bucket we are uploading the files into</li>
 * <li>directory: directory to upload</li>
 * </ul>
 */
public class S3UploadJobRunner implements JobRunner {
    public static String KEY = "com.atlassian.migration.datacenter.fs.S3UploadJobRunner";
    private static Logger log = LoggerFactory.getLogger(S3UploadJobRunner.class);
    private final FilesystemMigrationService s3Service;
    private MultipleFileUpload upload;

    public S3UploadJobRunner(FilesystemMigrationService fsMigrationService) {
        this.s3Service = fsMigrationService;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        if (s3Service.isRunning()) {
            return JobRunnerResponse.aborted("S3 upload job is still running");
        }

        // Get schedule job configuration
        final Map<String, Serializable> parameters = jobRunnerRequest.getJobConfig().getParameters();
        if (parameters.containsKey("s3bucket") && parameters.containsKey("directory")) {
            final FilesystemMigrationConfig config = new FilesystemMigrationConfig(
                    (String) parameters.get("s3bucket"), (String) parameters.get("directory"));

            log.debug("Starting S3 migration with config {}", config);
            s3Service.startMigration();
            return JobRunnerResponse.success("S3 upload was successful.");
        } else {
            return JobRunnerResponse.failed("S3 upload configuration is missing.");
        }
    }

}
