package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3UploadListener implements S3ProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(S3UploadListener.class);

    private long bytesTransferred = 0L;

    @Override
    public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        bytesTransferred += progressEvent.getBytesTransferred();
        logger.info("Transferred {} bytes", bytesTransferred);
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }
}
