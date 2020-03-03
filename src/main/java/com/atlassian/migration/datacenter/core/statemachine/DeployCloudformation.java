package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.statemachine.State;
import software.amazon.awssdk.services.cloudformation.model.StackInstanceNotFoundException;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

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
        try {
            StackStatus status = cloudformationApi.getStatus(context.getAppStackName());
            return status != null && isTerminalStackStatus(status);
        } catch (StackInstanceNotFoundException sinfe) {
            return false;
        }
    }

    private boolean isTerminalStackStatus(StackStatus status) {
        return status.equals(StackStatus.CREATE_COMPLETE) || status.equals(StackStatus.CREATE_FAILED);
    }
}
