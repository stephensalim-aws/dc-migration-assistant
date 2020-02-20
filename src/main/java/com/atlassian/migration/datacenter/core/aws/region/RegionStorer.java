package com.atlassian.migration.datacenter.core.aws.region;

public interface RegionStorer {

    void storeRegion(String string) throws InvalidAWSRegionException;

}
