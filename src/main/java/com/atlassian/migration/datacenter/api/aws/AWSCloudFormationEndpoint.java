package com.atlassian.migration.datacenter.api.aws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aws/cloudformation")
public class AWSCloudFormationEndpoint {



    /**
     * @return A response with all AWS regions
     */
    @GET
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStack(){
        return Response.ok().build();
    }

}
