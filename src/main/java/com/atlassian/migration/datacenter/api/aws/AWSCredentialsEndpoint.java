package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.auth.CredentialsStorer;
import com.atlassian.migration.datacenter.core.aws.auth.ProbeAWSAuth;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("aws/credentials")
public class AWSCredentialsEndpoint {

    private final CredentialsStorer credentialsStorer;
    private final ProbeAWSAuth probe;

    @Autowired
    public AWSCredentialsEndpoint(CredentialsStorer credentialsStorer, ProbeAWSAuth probe) {
        this.credentialsStorer = credentialsStorer;
        this.probe = probe;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeAWSCredentials(AWSCredentialsWebObject credentials) {
        credentialsStorer.storeAccessKeyId(credentials.getAccessKeyId());
        credentialsStorer.storeSecretAccessKey(credentials.getSecretAccessKey());

        return Response
                .noContent()
                .build();
    }

    @POST
    @Path("v1/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testCredentialsSDKV1() {
        return Response.ok(probe.probeSDKV1()).build();
    }

    @POST
    @Path("v2/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testCredentialsSDKV2() {
        return Response.ok(probe.probeSDKV2()).build();
    }
}
