package com.heatledger.embed.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.heatledger.embed.sample.Models.JSONSuccess;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "Sample Resource")
@Path("/sample")
public class ResourceSample {

    @GET
    @Path("/search/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Search sample", response = Models.JSONSuccess.class)    
    public String search(            
            @ApiParam(value = "query")
                @PathParam("query") String queryParam) {
        return JSONSuccess.toJSON().toJSONString();
    }
}