package com.atlassian.migration.datacenter.spi;

import java.util.Map;

public interface FailableOperation {
    Map<String, Exception> getFailed();
}
