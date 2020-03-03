package com.atlassian.migration.datacenter.core.aws.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Copyright Atlassian: 03/03/2020
 */
public class PostgresMigration
{
    private static String pddumpPaths[] = {"/usr/bin/pg_dump", "/usr/local/bin/pg_dump"};

    private Optional<String> getPgdumpPath() {
        for (String path : pddumpPaths) {
            Path p = Paths.get(path);
            if (Files.isReadable(p) &&Files.isExecutable(p)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }

}
