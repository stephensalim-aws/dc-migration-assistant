package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.auth.ReadCredentialsService;
import com.atlassian.migration.datacenter.core.aws.auth.WriteCredentialsService;
import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("aws/credentials")
public class AWSCredentialsEndpoint {

    private final WriteCredentialsService writeCredentialsService;
    private final ReadCredentialsService readCredentialsService;
    private final ProbeAWSAuth probe;

    @Autowired
    public AWSCredentialsEndpoint(WriteCredentialsService writeCredentialsService, ReadCredentialsService readCredentialsService, ProbeAWSAuth probe) {
        this.writeCredentialsService = writeCredentialsService;
        this.readCredentialsService = readCredentialsService;
        this.probe = probe;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response storeAWSCredentials(AWSCredentialsWebObject credentials) {
        writeCredentialsService.storeAccessKeyId(credentials.getAccessKeyId());
        writeCredentialsService.storeSecretAccessKey(credentials.getSecretAccessKey());

        return Response
                .noContent()
                .build();
    }

    @POST
    @Path("v1/test")
    @Produces(APPLICATION_JSON)
    public Response testCredentialsSDKV1() {
        return Response.ok(probe.probeSDKV1()).build();
    }

    @POST
    @Path("v2/test")
    @Produces(APPLICATION_JSON)
    public Response testCredentialsSDKV2() {
        return Response.ok(probe.probeSDKV2()).build();
    }

    @JsonAutoDetect
    static class AWSCredentialsWebObject {

        private String accessKeyId;
        private String secretAccessKey;
        private String region;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public String getRegion() {
            return region;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

}
