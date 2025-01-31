package com.redhat.application.resource;

import java.util.List;

import com.redhat.application.service.PresentationService;
import com.redhat.domain.model.Presentation;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPresentations() {
        List<Presentation> presentations = service.findAllPresentations();

        if (presentations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"There are no registered presentations.\"}")
                    .build();
        }
    
        return Response.ok(presentations).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPresentationById(@PathParam("id") Long id) {
        Presentation presentation = service.findPresentationById(id);

        if (presentation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Presentation not found\"}")
                    .build();
        }
    
        return Response.ok(presentation).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPresentation(@Valid Presentation presentation) {
        service.createPresentation(presentation);
        return Response.status(Response.Status.CREATED).entity(presentation).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePresentationById(@PathParam("id") Long id) {
        var deleted = service.deletePresentationById(id);
        
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Presentation not found.\"}")
                    .build();
        }
        
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
