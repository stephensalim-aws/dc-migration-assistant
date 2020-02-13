package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;
import com.aws.apn.migration.awsmigrationplugin.spi.fs.FilesystemMigrationProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3UploadListener implements S3ProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(S3UploadListener.class);

    private FilesystemMigrationProgress progress;

    public S3UploadListener(FilesystemMigrationProgress progress) {
        this.progress = progress;
    }

    @Override
    public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long total = progress.addBytes(progressEvent.getBytesTransferred());
        if (total != 0) {
            logger.info("Transferred {} bytes", total);
        }
    }

    public long getBytesTransferred() {
        return progress.getBytesTransferred();
    }
}
