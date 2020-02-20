package com.atlassian.migration.datacenter.core.aws.auth;

public interface RegionStorer {

    void storeRegion(String string) throws InvalidAWSRegionException;

}
