package com.atlassian.migration.datacenter.spi.statemachine;

/**
 * Represents a state in the finite state machine modelling the migration. The state should be responsible for
 * executing one component of the migration, defining the next state, and the conditions that can be met before a
 * transition to the next state can occur.
 */
public interface State {

    /**
     * Runs this step of the migration. Is called when entering this state
     */
    void run();

    /**
     * Determines the next state to transition to. Should do any checks required to verify a valid transition
     * can occur. Can also be used as a hook for actions to take when exiting a state.
     * @return the state that should follow this one.
     * @throws NoValidTransitionException when the conditions are not valid for any transition or an error
     * occurs while processing the on-exit hook.
     */
    State nextState() throws NoValidTransitionException;

    /**
     * Can be used to determine if now is a valid time to request a transition to the next state.
     */
    boolean readyToTransition();

}
