package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.FailableOperation;

import java.nio.file.Path;
import java.util.Queue;

public interface Uploader extends FailableOperation {
    void upload(Queue<Path> queue);
}
