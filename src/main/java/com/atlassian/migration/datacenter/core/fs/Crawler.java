package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.fs.FailedFileMigrationReport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface Crawler {
    void crawlDirectory(Path start, ConcurrentLinkedQueue<Path> queue) throws IOException;
}
