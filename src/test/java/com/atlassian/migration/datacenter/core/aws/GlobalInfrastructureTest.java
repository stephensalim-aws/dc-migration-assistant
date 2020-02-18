package com.atlassian.migration.datacenter.core.aws;

import com.amazonaws.regions.Regions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;


public class GlobalInfrastructureTest {

    GlobalInfrastructure sut;

    @BeforeEach
    public void initialiseSut() {
        sut = new GlobalInfrastructure();
    }

    @Test
    public void itShouldReturnAllRegions() {
        List<String> regions = sut.getRegions();
        System.out.println(regions);

        List<String> allRegions = Arrays.stream(Regions.values())
                .map(Regions::getName)
                .collect(Collectors.toList());

        assertIterableEquals(allRegions, regions);
    }

}
