package com.redhat.infrastructure.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.domain.model.Presentation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class PresentationClient {

    @ConfigProperty(name = "api.presentation.url")
    String presentationApiUrl;

    public Presentation getPresentationById(Long id) {
        Client client = ClientBuilder.newClient();
        
        WebTarget target = client.target(presentationApiUrl + "/presentation/" + id);

        System.out.println("REQUEST: " + presentationApiUrl + "/presentation/" + id);

        try {
            return target.request(MediaType.APPLICATION_JSON).get(Presentation.class);
        } finally {
            client.close();
        }
    }
}
