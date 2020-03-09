package com.atlassian.migration.datacenter.core.aws.db;

import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright Atlassian: 09/03/2020
 */
@Testcontainers
class PostgresMigrationIT
{
    @Container
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
        .withDatabaseName("jira")
        .withCopyFileToContainer(MountableFile.forClasspathResource("jira.sql"), "/docker-entrypoint-initdb.d/");

    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException
    {
        Properties props = new Properties();
        props.put("user", postgres.getUsername());
        props.put("password", postgres.getPassword());
        conn = DriverManager.getConnection(postgres.getJdbcUrl(), props);
    }

    @AfterEach
    void tearDown() throws SQLException
    {
        conn.close();
    }

    @Test
    void testPsqlDataImported() throws SQLException
    {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("SELECT id, summary FROM jiraissue WHERE issuenum = 1;");
        assertTrue(r.next());
        String summary = r.getString(2);
        assertTrue(summary.startsWith("As an Agile team, I'd like to learn about Scrum"));
        assertFalse(r.next());
        r.close();
        s.close();
        conn.close();
    }

}