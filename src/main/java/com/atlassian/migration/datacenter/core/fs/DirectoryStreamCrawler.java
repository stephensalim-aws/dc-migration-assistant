package com.atlassian.migration.datacenter.core.fs;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DirectoryStreamCrawler implements Crawler {

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
                    e.printStackTrace();
                }
            } else {
                queue.add(p);
            }
        });
    }
}
