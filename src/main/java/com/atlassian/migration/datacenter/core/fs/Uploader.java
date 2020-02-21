package com.atlassian.migration.datacenter.core.fs;

import java.nio.file.Path;
import java.util.Queue;

public interface Uploader {
    void upload(Queue<Path> queue);
}
