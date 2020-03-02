package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.spi.statemachine.State;

public class WaitCloudformation implements State {


    @Override
    public void run() {

    }

    @Override
    public State nextState() {
        return null;
    }

    @Override
    public boolean readyToTransition() {
        return false;
    }
}
