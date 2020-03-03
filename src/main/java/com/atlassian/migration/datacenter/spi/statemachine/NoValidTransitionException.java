package com.atlassian.migration.datacenter.spi.statemachine;

/**
 * This exception should be thrown when a state is asked to supply its next state but there is no valid transition
 * available currently.
 * @see State#nextState()
 * @see State#readyToTransition()
 */
public class NoValidTransitionException extends Exception {

    private State source;
    private String reason;

    public NoValidTransitionException(State source, String reason) {
        this.source = source;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("Unable to transition from state %s: %s", source, reason);
    }
}
