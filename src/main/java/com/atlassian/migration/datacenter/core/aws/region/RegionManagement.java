package com.atlassian.migration.datacenter.core.aws.region;

public interface RegionManagement {

    String AWS_REGION_PLUGIN_STORAGE_KEY = "com.atlassian.migration.datacenter.core.aws.region";
    String REGION_PLUGIN_STORAGE_SUFFIX = ".region";

    void storeRegion(String string) throws InvalidAWSRegionException;

    String getRegion();
}
