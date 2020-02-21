package com.atlassian.migration.datacenter.core.fs;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;
import com.atlassian.migration.datacenter.spi.fs.FilesystemMigrationProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3UploadListener implements S3ProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(S3UploadListener.class);

    private FilesystemMigrationProgress progress;

    S3UploadListener(FilesystemMigrationProgress progress) {
        this.progress = progress;
    }

    @Override
    public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long total = progress.addBytes(progressEvent.getBytesTransferred());
        logger.trace("Transferred {} B to S3", total);
    }

    long getBytesTransferred() {
        return progress.getBytesTransferred();
    }
}
