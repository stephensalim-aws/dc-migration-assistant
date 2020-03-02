package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.statemachine.State;

public class DeployCloudformation implements State {

    private final AWSMigrationContext context;
    private final CfnApi cloudformationApi;

    public DeployCloudformation(AWSMigrationContext context, CfnApi cloudformationApi) {
        this.context = context;
        this.cloudformationApi = cloudformationApi;
    }

    @Override
    public void run() {
        cloudformationApi.provisionStack(context.getAppTemplateURL(), context.getAppStackName(), context.getAppTemplateParameters());
    }

    @Override
    public State nextState() {
        return new WaitCloudformation();
    }

    @Override
    public boolean readyToTransition() {
        cloudformationApi.getStatus(context.getAppStackName());
        return true;
    }
}
