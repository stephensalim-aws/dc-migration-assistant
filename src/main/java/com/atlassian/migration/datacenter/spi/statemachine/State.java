package com.atlassian.migration.datacenter.spi.statemachine;

/**
 * Represents a state in the finite state machine modelling the migration. The state should be responsible for
 * executing one component of the migration, defining the next state, and the conditions that can be met before a
 * transition to the next state can occur.
 */
public interface State {

    /**
     * Runs this step of the migration.
     */
    void run();

    State nextState() throws NoValidTransitionException;

    boolean readyToTransition();

}
