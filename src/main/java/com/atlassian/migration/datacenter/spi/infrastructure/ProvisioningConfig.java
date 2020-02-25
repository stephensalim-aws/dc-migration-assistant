package com.atlassian.migration.datacenter.spi.infrastructure;


import java.util.Map;

public class ProvisioningConfig {

    private String templateUrl;
    private String stackName;
    private Map<String, String> params;

    public ProvisioningConfig() {
    }

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
