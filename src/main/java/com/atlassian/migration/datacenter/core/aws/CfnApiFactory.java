package com.atlassian.migration.datacenter.core.aws;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Copyright Atlassian: 26/02/2020
 */
@Component
public class CfnApiFactory
{
    private Optional<CfnApi> cfnApi;

    /**
     * Fetch a CfnApi implementation.
     *
     * FIXME: We should implement a teardown of the object once finished?
     */
    public CfnApi getCfnApi() {
        if (cfnApi.isPresent()) {
            return cfnApi.get();
        }

        // FIXME: This could potentially be a BeanFactory lookup, but we currently only have a single impl.
        CfnApi api = new CfnApi();
        this.cfnApi = Optional.of(api);
        return api;
    }

}
