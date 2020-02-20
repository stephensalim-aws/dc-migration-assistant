package com.atlassian.migration.datacenter.core.aws.region;

public class InvalidAWSRegionException extends Exception {
    @Override
    public String getMessage() {
        return "Must be a valid AWS region";
    }
}
