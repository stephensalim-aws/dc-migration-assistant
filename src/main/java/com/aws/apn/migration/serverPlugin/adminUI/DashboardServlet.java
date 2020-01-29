package com.aws.apn.migration.serverPlugin.adminUI;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

public class DashboardServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DashboardServlet.class);
    private static final String PLUGIN_STORAGE_KEY = "com.aws.apn.migration.serverPlugin";
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer renderer;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public DashboardServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUser(request).getUsername();

        if (username == null || !userManager.isSystemAdmin(userManager.getRemoteUserKey(request))) {
            redirectToLogin(request, response);
            return;
        }

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

        pluginSettings.put(PLUGIN_STORAGE_KEY + ".accessKey", request.getParameter("AccessKey"));
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".secretKey", request.getParameter("SecretKey"));
        pluginSettings.put(PLUGIN_STORAGE_KEY + ".awsRegion", request.getParameter("AWSRegion"));

        response.setContentType("text/html;charset=utf-8");
        renderer.render("dashboard.vm", response.getWriter());
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}
