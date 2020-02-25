package com.atlassian.migration.datacenter.core.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DirectoryStreamCrawler implements Crawler {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryStreamCrawler.class);
    // TODO storing the full Exception might have impact to memory footprint
    // We should assess the impact after running some larger migration
    private final Map<String, Exception> failedPaths = new HashMap<>();

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
                    failedPaths.put(p.toString(), e);
                }
            } else {
                queue.add(p);
            }
        });
    }

    @Override
    public Map<String, Exception> getFailed() {
        return Collections.unmodifiableMap(failedPaths);
    }
}
