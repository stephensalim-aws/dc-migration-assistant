package com.atlassian.migration.datacenter.core.fs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrawlerTest {
    @TempDir
    Path tempDir;

    private Crawler crawler;
    private ConcurrentLinkedQueue<Path> queue;
    private Set<Path> expectedPaths;

    @BeforeEach
    void createFiles() throws Exception {
        queue = new ConcurrentLinkedQueue<>();
        expectedPaths = new HashSet<>();

        expectedPaths.add(Files.write(tempDir.resolve("newfile.txt"), "newfile content".getBytes()));
        final Path subdirectory = Files.createDirectory(tempDir.resolve("subdirectory"));
        expectedPaths.add(Files.write(subdirectory.resolve("subfile.txt"), "subfile content in the subdirectory".getBytes()));
    }

    @Test
    void shouldListAllSubdirectories() throws Exception {
        crawler = new DirectoryStreamCrawler();
        crawler.crawlDirectory(tempDir, queue);

        expectedPaths.forEach(path -> assertTrue(queue.contains(path), String.format("Expected %s is absent from crawler queue", path)));
    }
}
