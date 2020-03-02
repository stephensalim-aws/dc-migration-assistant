package com.atlassian.migration.datacenter.core.statemachine;

import com.atlassian.migration.datacenter.spi.statemachine.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeployCloudformationTest {
    private DeployCloudformation sut;

    @BeforeEach
    void setUp() {
        sut = new DeployCloudformation();
    }

    @Test
    void shouldTransitionToCloudformationWaiting() {
        State nextState = sut.nextState();

        assertEquals(WaitCloudformation.class, nextState.getClass());
    }

}