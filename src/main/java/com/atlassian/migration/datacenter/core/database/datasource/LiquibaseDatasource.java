package com.atlassian.migration.datacenter.core.database.datasource;

import com.atlassian.jira.component.ComponentAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class LiquibaseDatasource {

    private DataSource ds;

    @PostConstruct
    public void init() {
        ds = ComponentAccessor.getOSGiComponentInstanceOfType(DataSource.class);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
