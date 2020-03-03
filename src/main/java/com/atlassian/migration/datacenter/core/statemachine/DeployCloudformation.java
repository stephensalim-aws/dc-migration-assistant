package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.statemachine.NoValidTransitionException;
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
    public State nextState() throws NoValidTransitionException {
        StackStatus status = safeGetStackStatus();

        throwNoValidTransitionIfStatusIsNull(status);

        switch (status) {
            case CREATE_COMPLETE:
                return new DeployMigrationStack();
            case CREATE_FAILED:
                return new ErrorState();
            default:
                throw new NoValidTransitionException(this, "Can only transition when stack deployment has completed");
        }
    }

    private void throwNoValidTransitionIfStatusIsNull(StackStatus status) throws NoValidTransitionException {
        if (status == null) {
            throw new NoValidTransitionException(this, "Can only transition when stack deployment has completed");
        }
    }

    @Override
    public boolean readyToTransition() {
        StackStatus status = safeGetStackStatus();
        return status != null && isTerminalStackStatus(status);
    }

    private StackStatus safeGetStackStatus() {
        try {
            StackStatus status = cloudformationApi.getStatus(context.getAppStackName());
            return status;
        } catch (StackInstanceNotFoundException sinfe) {
            return null;
        }
    }

    private boolean isTerminalStackStatus(StackStatus status) {
        return status.equals(StackStatus.CREATE_COMPLETE) || status.equals(StackStatus.CREATE_FAILED);
    }
}
