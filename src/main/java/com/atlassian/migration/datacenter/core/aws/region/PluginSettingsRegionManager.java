package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginSettingsRegionManager implements RegionStorer, RegionFetcher {

    private static final String AWS_REGION_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.region";
    private static final String REGION_PLUGIN_STORAGE_SUFFIX = ".region";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final GlobalInfrastructure globalInfrastructure;

    @Autowired
    public PluginSettingsRegionManager(@ComponentImport PluginSettingsFactory pluginSettingsFactory, GlobalInfrastructure globalInfrastructure) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.globalInfrastructure = globalInfrastructure;
    }

    @Override
    public String getRegion() {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        return (String) pluginSettings.get(AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX);
    }

    @Override
    public void storeRegion(String region) throws InvalidAWSRegionException {
        if(!isValidRegion(region)) {
            throw new InvalidAWSRegionException();
        }

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX, region);
    }

    private boolean isValidRegion(String testRegion) {
        return globalInfrastructure.
                getRegions()
                .stream()
                .anyMatch(region -> region.equals(testRegion));
    }
}
