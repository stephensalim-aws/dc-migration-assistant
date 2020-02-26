package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.fs.FailedFileMigrationReport;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Uploader {
    void upload(ConcurrentLinkedQueue<Path> queue, AtomicBoolean isCrawlDone);
}
