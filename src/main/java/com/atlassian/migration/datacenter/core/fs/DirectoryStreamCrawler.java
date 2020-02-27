package com.atlassian.migration.datacenter.core.fs;

import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport;
import com.atlassian.migration.datacenter.spi.fs.reporting.FileSystemMigrationErrorReport.FailedFileMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DirectoryStreamCrawler implements Crawler {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryStreamCrawler.class);

    private FileSystemMigrationErrorReport report;

    public DirectoryStreamCrawler(FileSystemMigrationErrorReport report) {
        this.report = report;
    }

    @Override
    public void crawlDirectory(Path start, ConcurrentLinkedQueue<Path> queue) throws IOException {
        final DirectoryStream<Path> paths;
        paths = Files.newDirectoryStream(start);
        listDirectories(queue, paths);
    }

    private void listDirectories(ConcurrentLinkedQueue<Path> queue, DirectoryStream<Path> paths) {
        paths.forEach(p -> {
            if (Files.isDirectory(p)) {
                try (final DirectoryStream<Path> newPaths = Files.newDirectoryStream(p.toAbsolutePath())) {
                    listDirectories(queue, newPaths);
                } catch (IOException e) {
                    logger.error("Error when traversing directory {}, with exception {}", p, e);
                    report.reportFileNotMigrated(new FailedFileMigration(p, e.getMessage()));
                }
            } else {
                queue.add(p);
            }
        });
    }
}