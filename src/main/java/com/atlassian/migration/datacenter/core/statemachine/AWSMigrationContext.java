package com.atlassian.migration.datacenter.core.statemachine;

import net.java.ao.Entity;

import java.util.Map;

public interface AWSMigrationContext extends Entity {

    /**
     * AppStackName is the property used to determine the name of the AWS Cloudformation Stack
     * used to run the target application
     * @return the AppStackName
     */
    String getAppStackName();

    /**
     * @see AWSMigrationContext#getAppStackName()
     */
    void setAppStackName(String appStackName);

    /**
     * AppTemplateURL is the property used as the URL of the Cloudformation template which
     * deploys the target application
     * @return the AppTemplateURL
     */
    String getAppTemplateURL();

    /**
     * @see AWSMigrationContext#getAppTemplateURL()
     */
    void setAppTemplateURL(String appTemplateURL);

    /**
     * AppTemplateParameters is the property used to fill the template parameters
     * for the App Cloudformation Template. The key should be the Cloudformation Parameter
     * name and the value should be a string representation of the parameter value.
     * @see AWSMigrationContext#getAppTemplateURL()
     * @return the AppTemplateParameters
     */
    Map<String, String> getAppTemplateParameters();

    /**
     * @see AWSMigrationContext#getAppTemplateParameters()
     */
    void setAppTemplateParameters(Map<String, String> parameters);

}
