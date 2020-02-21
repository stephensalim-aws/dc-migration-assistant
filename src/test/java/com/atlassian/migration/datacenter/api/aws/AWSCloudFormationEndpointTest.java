package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import com.atlassian.migration.datacenter.core.aws.auth.ReadCredentialsService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class AWSCloudFormationEndpointTest {

    private AWSCloudFormationEndpoint cloudFormationEndpoint;
    private RegionService regionManagement;
    private ReadCredentialsService credentialStorage;

    @Mock
    private PluginSettingsFactory pluginSettings;

    @BeforeEach
    public void initializeCloudFormationEndpoint() {
        CfnApi cfnApi = new CfnApi();
        regionManagement = new RegionService() {
            @Override
            public void storeRegion(String string) throws InvalidAWSRegionException {
            }

            @Override
            public String getRegion() {
                return "eu-central-1";
            }
        };

        credentialStorage = new ReadCredentialsService() {
            @Override
            public String getAccessKeyId() {
                return "";
            }

            @Override
            public String getSecretAccessKey() {
                return "";
            }
        };

        AtlassianPluginAWSCredentialsProvider credentialsProvider = new AtlassianPluginAWSCredentialsProvider(this.credentialStorage);
        cloudFormationEndpoint = new AWSCloudFormationEndpoint(credentialsProvider, this.regionManagement, cfnApi);
    }

    @Test
    public void itShouldReturnAllQSParams() {
        List<String> params = cloudFormationEndpoint.getQuickStartParams();
        assertEquals(59, params.size());
    }

    @Test
    public void itShouldReturnAValidStackStatus() {
        String dummyStackName = "SOME_STACK";
        Response response = this.cloudFormationEndpoint.retrieveStackStatus(dummyStackName);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void itShouldFailToStartQuickStartStack() {
        StringBuilder contentBuilder = new StringBuilder();
        ClassLoader classLoader = AWSCloudFormationEndpointTest.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("cfn/cfn-stack-create.json"))
                .getFile());
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
        String requestBody = contentBuilder.toString();

        Response response = this.cloudFormationEndpoint.createStack(requestBody);
        Optional<String> body = (Optional<String>) response.getEntity();
        assertFalse(body.isPresent());
    }

}
