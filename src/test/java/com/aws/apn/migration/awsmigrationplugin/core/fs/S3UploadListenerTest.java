package com.aws.apn.migration.awsmigrationplugin.core.fs;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class S3UploadListenerTest {
    @Test
    public void transferredBytesAreAccessibleFromListener() {
        // given
        long transferredBytes = 1000;
        ProgressEvent progressEvent = new ProgressEvent(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, transferredBytes);
        S3UploadListener listener = new S3UploadListener();
        // when
        listener.progressChanged(progressEvent);
        // then
        Assertions.assertEquals(transferredBytes, listener.getBytesTransferred());
    }
}
