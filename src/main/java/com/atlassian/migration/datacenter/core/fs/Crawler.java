package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.FailableOperation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface Crawler extends FailableOperation {
    void crawlDirectory(Path start, ConcurrentLinkedQueue<Path> queue) throws IOException;
}
