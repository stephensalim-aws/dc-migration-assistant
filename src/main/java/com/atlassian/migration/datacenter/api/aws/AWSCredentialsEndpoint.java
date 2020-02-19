package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.auth.CredentialsStorer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("aws/credentials")
public class AWSCredentialsEndpoint {

    CredentialsStorer credentialsStorer;

    @Autowired
    public AWSCredentialsEndpoint(CredentialsStorer credentialsStorer) {
        this.credentialsStorer = credentialsStorer;
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
}
