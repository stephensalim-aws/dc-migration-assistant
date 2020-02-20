package com.atlassian.migration.datacenter.core.aws.auth;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for managing the storage and retrieval of AWS Credentials. Should not be used for direct access to credentials
 * except for in a CredentialsProvider implementation. This class stores credentials insecurely through pluginsettings
 * and should not be used in production.
 */
@Component
public class PlainTextCredentialsManager implements CredentialsFetcher, CredentialsStorer, RegionFetcher, RegionStorer {

    private static final String AWS_CREDS_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.auth";
    private static final String ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX = ".accessKeyId";
    private static final String SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX = ".secretAccessKey";
    private static final String REGION_PLUGIN_STORAGE_SUFFIX = ".region";

    private final GlobalInfrastructure globalInfrastructure;
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public PlainTextCredentialsManager(@ComponentImport PluginSettingsFactory pluginSettingsFactory, GlobalInfrastructure globalInfrastructure) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.globalInfrastructure = globalInfrastructure;
    }

    @Override
    public String getAccessKeyId() {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        return (String) pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX);
    }

    @Override
    public String getSecretAccessKey() {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        return (String) pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX);
    }

    @Override
    public String getRegion() {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        return (String) pluginSettings.get(AWS_CREDS_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX);
    }

    @Override
    public void storeAccessKeyId(String accessKeyId) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + ACCESS_KEY_ID_PLUGIN_STORAGE_SUFFIX, accessKeyId);
    }

    @Override
    public void storeSecretAccessKey(String secretAccessKey) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + SECRET_ACCESS_KEY_PLUGIN_STORAGE_SUFFIX, secretAccessKey);
    }

    @Override
    public void storeRegion(String region) throws InvalidAWSRegionException {
        if(!isValidRegion(region)) {
            throw new InvalidAWSRegionException();
        }

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(AWS_CREDS_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX, region);
    }

    private boolean isValidRegion(String testRegion) {
        return globalInfrastructure.
                getRegions()
                .stream()
                .anyMatch(region -> region.equals(testRegion));
    }
}
