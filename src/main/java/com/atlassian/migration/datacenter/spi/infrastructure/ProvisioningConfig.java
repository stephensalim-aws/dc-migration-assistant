package com.atlassian.migration.datacenter.spi.infrastructure;


import java.util.Map;

public class ProvisioningConfig {

    private final String templateUrl;
    private final String stackName;
    private final Map<String,String> params;

    public ProvisioningConfig(String templateUrl, String stackName, Map<String, String> params) {
        this.templateUrl = templateUrl;
        this.stackName = stackName;
        this.params = params;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public String getStackName() {
        return stackName;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
