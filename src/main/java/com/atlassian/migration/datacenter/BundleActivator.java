package com.atlassian.migration.datacenter;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Dictionary;
import java.util.Hashtable;

public class BundleActivator implements org.osgi.framework.BundleActivator {

    private final static Logger LOGGER = Logger.getLogger(BundleActivator.class);

    /**
     * Default constructor
     */
    public BundleActivator() {
    }

    /**
     * Standard OSGI startup hook
     *
     * @param bundleContext the bundle context
     */
    public void start(BundleContext bundleContext) {
        try {
            InitialContext ctx = new javax.naming.InitialContext();
            DataSource o = (DataSource) ctx.lookup("java:comp/env/jdbc/JiraDS");
            Dictionary<String, String> properties = new Hashtable<>();
            bundleContext.registerService(DataSource.class, o, properties);
            LOGGER.warn("registered datasource");
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }


    /**
     * Standard OSGI shutdown hook
     *
     * @param bundleContext the bundle context
     */
    public void stop(BundleContext bundleContext) {
        //does nothing
    }
}