package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.spi.statemachine.NoValidTransitionException;
import com.atlassian.migration.datacenter.spi.statemachine.State;

public class DeployMigrationStack implements State {
    @Override
    public void run() {

    }

    @Override
    public State nextState() throws NoValidTransitionException {
        return null;
    }

    @Override
    public boolean readyToTransition() {
        return false;
    }
}
