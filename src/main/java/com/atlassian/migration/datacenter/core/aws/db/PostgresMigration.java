package com.atlassian.migration.datacenter.core.aws.db;

import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration;
import com.atlassian.migration.datacenter.core.application.DatabaseConfiguration;
import com.atlassian.migration.datacenter.core.exceptions.DatabaseMigrationFailure;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Copyright Atlassian: 03/03/2020
 */
public class PostgresMigration
{
    private ApplicationConfiguration applicationConfiguration;

    private static String pddumpPaths[] = {"/usr/bin/pg_dump", "/usr/local/bin/pg_dump"};

    public PostgresMigration(ApplicationConfiguration applicationConfiguration)
    {
        this.applicationConfiguration = applicationConfiguration;
    }

    private Optional<String> getPgdumpPath() {
        for (String path : pddumpPaths) {
            Path p = Paths.get(path);
            if (Files.isReadable(p) &&Files.isExecutable(p)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }

    private Process dumpDatabase(File to) throws DatabaseMigrationFailure
    {
        String pgdump = getPgdumpPath()
            .orElseThrow(() -> new DatabaseMigrationFailure("Failed to find appropriate pg_dump executable."));

        DatabaseConfiguration config = applicationConfiguration.getDatabaseConfiguration();

        ProcessBuilder builder = new ProcessBuilder(pgdump,
                                                    "--create",
                                                    "--no-owner",
                                                    "--compress=9",
                                                    "--dbname", config.getName(),
                                                    "--host", config.getHost(),
                                                    "--username", config.getUsername())
            .redirectOutput(Redirect.to(to));
        builder.environment().put("PGPASSWORD", config.getPassword());

        try {
            return builder.start();
        } catch (IOException e) {
            String command = String.join(" ", builder.command());
            throw new DatabaseMigrationFailure("Failed to start pg_dump process with commandline: "+command, e);
        }
    }
}
