package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.spi.statemachine.State;

public class DeployCloudformation implements State {
    @Override
    public void run() {

    }

    @Override
    public State nextState() {
        return new WaitCloudformation();
    }

    @Override
    public boolean readyToTransition() {
        return false;
    }
}
