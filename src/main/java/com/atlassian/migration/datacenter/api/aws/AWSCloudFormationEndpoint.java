package com.atlassian.migration.datacenter.api.aws;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.TemplateParameter;
import com.amazonaws.services.cloudformation.model.ValidateTemplateRequest;
import com.amazonaws.services.cloudformation.model.ValidateTemplateResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudformation.model.Parameter;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/aws/cloudformation")
@Component
public class AWSCloudFormationEndpoint {

    private final static String QS_URL = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";
    private final static Logger LOGGER = Logger.getLogger(AWSCloudFormationEndpoint.class);
    private final AtlassianPluginAWSCredentialsProvider credentialsProvider;
    private final RegionService regionManagement;
    private final CfnApi cfnApi;

    @Inject
    public AWSCloudFormationEndpoint(AtlassianPluginAWSCredentialsProvider credentialsProvider, RegionService regionManagement, CfnApi cfnApi) {
        this.credentialsProvider = credentialsProvider;
        this.regionManagement = regionManagement;
        this.cfnApi = cfnApi;
    }

    private static Collection<Parameter> buildParameters() {
        return new ArrayList<>();
    }

    private static AmazonCloudFormation getCloudformationClient(final String region, final AtlassianPluginAWSCredentialsProvider credentialsProvider) {
        return AmazonCloudFormationClientBuilder.standard().withRegion(region).withCredentials(credentialsProvider).build();
    }

    private static AmazonS3 getS3Client(final String region, final AtlassianPluginAWSCredentialsProvider credentialsProvider) {
        return AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(credentialsProvider).build();
    }

    /**
     * @param rawJSON the json of the cloudformation parameters
     * @return returns the response of the cloudformation api service
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStack(String rawJSON) {
        final List<String> cfnParams = this.getQuickStartParams();
        try (Stream<String> paramStream = cfnParams.parallelStream()) {
            final JSONObject stackData = new JSONObject(rawJSON);
            final Collection<Parameter> parameters = paramStream.map(param -> {
                try {
                    return Parameter.builder().parameterKey(param).parameterValue(stackData.getString(param)).build();
                } catch (Exception e) {
                    LOGGER.error(e.getLocalizedMessage());
                    return null;
                }
            }).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            assert (parameters.size() == cfnParams.size());
            Optional<String> stack = this.cfnApi.provisionStack(QS_URL, "dc-migration-plugin-asi-stack", parameters);
            return Response.ok(stack).build();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            return Response.serverError().build();
        }
    }


    public List<String> getQuickStartParams() {
        AmazonCloudFormation cfn = getCloudformationClient(this.regionManagement.getRegion(), this.credentialsProvider);
        ValidateTemplateRequest validateTemplateRequest = new ValidateTemplateRequest();
        validateTemplateRequest.setTemplateURL(QS_URL);
        ValidateTemplateResult result = cfn.validateTemplate(validateTemplateRequest);
        List<TemplateParameter> parameterList = result.getParameters();
        return parameterList.parallelStream()
                .map(TemplateParameter::getParameterKey)
                .sorted()
                .collect(Collectors.toList());
    }

}
