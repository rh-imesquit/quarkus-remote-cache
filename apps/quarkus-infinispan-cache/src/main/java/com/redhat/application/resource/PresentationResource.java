package com.redhat.application.resource;

import com.redhat.application.service.PresentationService;
import com.redhat.domain.model.Presentation;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/presentation")
public class PresentationResource {
    @Inject
    PresentationService service;

    @GET
    @Path("/cache/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPresentationWithCache(@PathParam("key") Long key) {
        Presentation presentation = service.getPresentationFromCache(key);

        if (presentation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Presentation not found\"}")
                    .build();
        }
    
        return Response.ok(presentation).build();
    }

    @GET
    @Path("/nocache/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPresentationWithoutCache(@PathParam("id") Long key) {
        Presentation presentation = service.requestPresentationById(key);

        if (presentation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Presentation not found\"}")
                    .build();
        }

        System.out.println("PRESENTATION: " + presentation.toString());
    
        return Response.ok(presentation).build();
    }
}
