package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.Tag;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AWSCloudFormationEndpointTest {

    @InjectMocks
    private AWSCloudFormationEndpoint cloudFormationEndpoint;

    @Mock
    private CloudFormationAsyncClient cloudFormationAsyncClient;

    @InjectMocks
    private CfnApi cfnApi;

    @Test
    @Disabled("Ignoring until we can sort this test properly")
    public void itShouldFailToStartQuickStartStack() throws JSONException {
        String templateUrl = "https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml";
        String stackName = "dc-migration-plugin-asi-stack";
        List<Parameter> parameters = new ArrayList<>();

        Tag tag = Tag.builder()
                .key("created_by")
                .value("atlassian-awsmigration-plugin")
                .build();

        CreateStackRequest createStackRequest = CreateStackRequest.builder()
                .templateURL(templateUrl)
                .stackName(stackName)
                .parameters(parameters)
                .tags(tag)
                .build();

        when(this.cloudFormationAsyncClient.createStack(createStackRequest)).thenReturn(CompletableFuture.completedFuture(null));
        when(this.cfnApi.provisionStack(templateUrl, stackName, parameters)).thenReturn(Optional.empty());

        Response response = this.cloudFormationEndpoint.createStack("");
        Optional<String> body = (Optional<String>) response.getEntity();
        assertFalse(body.isPresent());


    }

}
