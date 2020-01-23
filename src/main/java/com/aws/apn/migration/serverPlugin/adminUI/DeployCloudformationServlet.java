package com.aws.apn.migration.serverPlugin.adminUI;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Scanned
public class DeployCloudformationServlet extends HttpServlet {
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
    public DeployCloudformationServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUser(request).getUsername();
        if (username == null || !userManager.isSystemAdmin(userManager.getRemoteUserKey(request))) {
            redirectToLogin(request, response);
            return;
        }

        Map<String, Object> context = new HashMap<String, Object>();
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

        String region = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".awsRegion");
        String secretKey = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".secretKey");
        String accessKey = (String) pluginSettings.get(PLUGIN_STORAGE_KEY + ".accessKey");

        pluginSettings.remove(PLUGIN_STORAGE_KEY + ".cloudformationStackId");

        createAtlassianKeyPair(region, accessKey, secretKey);
        context.put("availabilityZones", this.getAvailabilityZones(region, accessKey, secretKey));
        context.put("keyPairs", this.getEC2KeyPairs(region, accessKey, secretKey));
        context.put("instanceTypes", this.getAvailableInstanceTypes(region, accessKey, secretKey));


        response.setContentType("text/html;charset=utf-8");
        renderer.render("cloudformation.vm", context, response.getWriter());
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

    private List<String> getAvailabilityZones(final String region, final String accessKey, final String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        try (Stream<AvailabilityZone> zoneStream = zones_response.getAvailabilityZones().stream()) {
            return zoneStream.map(AvailabilityZone::getZoneName).sorted().collect(Collectors.toList());
        }
    }

    private List<String> getEC2KeyPairs(final String region, final String accessKey, final String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        DescribeKeyPairsResult describeKeyPairsResult = ec2.describeKeyPairs();
        try (Stream<KeyPairInfo> keyPairInfoStream = describeKeyPairsResult.getKeyPairs().stream()) {
            return keyPairInfoStream.map(KeyPairInfo::getKeyName).sorted().collect(Collectors.toList());
        }
    }

    private List<String> getAvailableInstanceTypes(final String region, final String accessKey, final String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        List<InstanceTypeInfo> typeList = ec2.describeInstanceTypes(new DescribeInstanceTypesRequest()).getInstanceTypes();
        try (Stream<InstanceTypeInfo> typeListStream = typeList.parallelStream()) {
            return typeListStream.map(InstanceTypeInfo::getInstanceType).distinct().sorted().collect(Collectors.toList());
        }
    }

    private void createAtlassianKeyPair(final String region, final String accessKey, final String secretKey) {
        final String keyName = "atl-migration-tool-".concat(region);
        if (!this.getEC2KeyPairs(region, accessKey, secretKey).contains(keyName)) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
            CreateKeyPairRequest keyPairRequest = new CreateKeyPairRequest().withKeyName(keyName);
            ec2.createKeyPair(keyPairRequest);
        }
    }
}
