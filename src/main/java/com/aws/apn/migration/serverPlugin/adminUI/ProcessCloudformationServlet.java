package com.aws.apn.migration.serverPlugin.adminUI;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@Scanned
public class ProcessCloudformationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProcessCloudformationServlet.class);
    private static final String PLUGIN_STORAGE_KEY = "com.aws.apn.migration.serverPlugin";
    private static final String TEMPLATE_URL = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer renderer;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public ProcessCloudformationServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public static String toCSV(String[] array) {
        String result = "";
        switch (array.length) {
            case 0: {
                return "";
            }
            case 1: {
                return array[0];
            }
            default: {
                StringBuilder sb = new StringBuilder();
                for (String s : array) {
                    sb.append(s).append(",");
                }
                result = sb.deleteCharAt(sb.length() - 1).toString();
                return result;
            }
        }

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

    private AmazonCloudFormation getCloudFormation(final String region, final String accessKey, final String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        AmazonCloudFormation cloudFormation = AmazonCloudFormationClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        return cloudFormation;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUser(request).getUsername();
        if (username == null || !userManager.isSystemAdmin(userManager.getRemoteUserKey(request))) {
            redirectToLogin(request, response);
            return;
        }

        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final Map<String, Object> context = new HashMap<>();
        final String region = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".awsRegion");
        final String secretKey = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".secretKey");
        final String accessKey = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".accessKey");

        final AmazonCloudFormation cloudFormation = this.getCloudFormation(region, accessKey, secretKey);

        EstimateTemplateCostResult estimateTemplateCostResult = cloudFormation.estimateTemplateCost(new EstimateTemplateCostRequest().withTemplateURL(TEMPLATE_URL)
                .withParameters(this.buildParameterList(request.getParameterNames(), request)));

        context.put("costEstimateURL", estimateTemplateCostResult.getUrl());

        String targetTemplate = "";

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".cloudformationStackId") == null ||
                pluginSettings.get(PLUGIN_STORAGE_KEY + ".cloudformationStackId").equals("")) {

            Gson gson = new Gson();

            try {
                CreateStackRequest createStackRequest = new CreateStackRequest().withStackName("jira-dc-autocreated")
                        .withTemplateURL(TEMPLATE_URL)
                        .withParameters(this.buildParameterList(request.getParameterNames(), request))
                        .withCapabilities(Capability.CAPABILITY_AUTO_EXPAND, Capability.CAPABILITY_NAMED_IAM)
                        .withDisableRollback(false)
                        .withEnableTerminationProtection(false);

                ValidateTemplateRequest validateTemplateRequest = new ValidateTemplateRequest().withTemplateURL(TEMPLATE_URL);

                ValidateTemplateResult validateTemplateResult = cloudFormation.validateTemplate(validateTemplateRequest);

                LOGGER.info(validateTemplateResult.getDescription());

                LOGGER.info(gson.toJson(createStackRequest));

                CreateStackResult createStackResult = cloudFormation.createStack(createStackRequest);

                pluginSettings.put(PLUGIN_STORAGE_KEY + ".cloudformationStackId", createStackResult.getStackId());

                context.put("cloudformationStackId", createStackResult.getStackId());
                targetTemplate = "cfncreated.vm";
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                renderer.render("error.vm", response.getWriter());
            }
        } else {
            context.put("cloudformationStackId", pluginSettings.get(PLUGIN_STORAGE_KEY + ".cloudformationStackId"));
            context.put("region", region);
            context.put("accessKey", accessKey);
            context.put("secretKey", secretKey);
            targetTemplate = "cfnstatus.vm";
        }
        context.put("region", region);
        context.put("accessKey", accessKey);
        context.put("secretKey", secretKey);
        response.setContentType("text/html;charset=utf-8");
        renderer.render(targetTemplate, context, response.getWriter());
    }

    private List<Parameter> buildParameterList(Enumeration<String> paramNames, HttpServletRequest request) {
        List<Parameter> parameters = new ArrayList<>();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!paramName.contains("Toggle")) {
                if (paramName.equals("AvailabilityZones")) {

                    Parameter parameter = new Parameter().withParameterKey(paramName).withParameterValue(toCSV(request.getParameterValues(paramName)));
                    parameters.add(parameter);
                } else {
                    Parameter parameter = new Parameter().withParameterKey(paramName).withParameterValue(request.getParameter(paramName));
                    parameters.add(parameter);
                }
            }
        }
        return parameters;
    }


}