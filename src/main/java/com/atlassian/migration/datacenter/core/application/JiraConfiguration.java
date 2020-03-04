package com.atlassian.migration.datacenter.core.application;

import com.atlassian.jira.config.util.JiraHome;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Copyright Atlassian: 04/03/2020
 */
@Component
public class JiraConfiguration implements ApplicationConfiguration
{
    private JiraHome jiraHome;
    private Optional<DatabaseConfiguration> databaseConfiguration = Optional.empty();

    public JiraConfiguration(JiraHome jiraHome)
    {
        this.jiraHome = jiraHome;
    }

    private String getXPath(String path, Path config) throws ConfigurationReadException
    {
        // We need to recreate the input stream for each read; not the most efficient,
        // but it's only done once and it's simpler than generating a full DOM.
        FileReader reader;
        try {
            reader = new FileReader(config.toFile());
        } catch (FileNotFoundException e) {
            throw new ConfigurationReadException("Failed to open dbconfig.xml", e);
        }

        InputSource inputSource = new InputSource(reader);
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {

            String val = xPath.evaluate(path, inputSource);
            // Note: XPath will return an empty string if not found;
            // we assume retrieved values can never be empty.
            if (val == null || val.equals(""))
                throw new ConfigurationReadException("No value found for XPath: "+path);

            return val;

        } catch (Exception e) {
            throw new ConfigurationReadException("Failed to parse dbconfig.xml, XPath: "+path, e);
        }
    }

    private DatabaseConfiguration parseDbConfig() throws ConfigurationReadException
    {
        Path dbconfig = Paths.get(jiraHome.getLocalHomePath()).resolve("dbconfig.xml");

        String username = getXPath("/jira-database-config/jdbc-datasource/username", dbconfig);
        String password = getXPath("/jira-database-config/jdbc-datasource/password", dbconfig);
        String urlStr = getXPath("/jira-database-config/jdbc-datasource/url", dbconfig);

        try {
            // JDBC URIs are absolute, so we need to parse them in 2 steps.
            URI absURI = URI.create(urlStr);
            URI dbURI = URI.create(absURI.getSchemeSpecificPart());
            String host = dbURI.getHost();
            String name = dbURI.getPath().substring(1); // Remove leading '/'

            return new DatabaseConfiguration(host, name, username, password);
        } catch (Exception e) {
            throw new ConfigurationReadException("Failed to parse dbconfig.xml", e);
        }

    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration() throws ConfigurationReadException
    {
        if (!databaseConfiguration.isPresent()) {
            databaseConfiguration = Optional.of(parseDbConfig());
        }

        return databaseConfiguration.get();
    }
}
