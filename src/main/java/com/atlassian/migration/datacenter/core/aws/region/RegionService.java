package com.atlassian.migration.datacenter.core.aws.region;

public interface RegionService {

    String getRegion();

    void storeRegion(String string) throws InvalidAWSRegionException;


}
