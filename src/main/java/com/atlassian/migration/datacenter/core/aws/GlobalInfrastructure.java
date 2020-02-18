package com.atlassian.migration.datacenter.core.aws;

import com.amazonaws.regions.Regions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalInfrastructure {

    public List<String> getRegions() {
        return Arrays.stream(Regions.values())
                .map(Regions::getName)
                .collect(Collectors.toList());
    }
}
