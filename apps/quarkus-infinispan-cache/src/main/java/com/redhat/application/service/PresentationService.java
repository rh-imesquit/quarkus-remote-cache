package com.redhat.application.service;

import com.redhat.domain.model.Presentation;
import com.redhat.infrastructure.cache.CacheService;
import com.redhat.infrastructure.client.PresentationClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PresentationService {
    @Inject
    CacheService cacheService;

    @Inject
    PresentationClient apiClient;

    public Presentation requestPresentationById(Long id) {
        return apiClient.getPresentationById(id);
    }
    
    public Presentation getPresentationFromCache (Long key) {
        System.out.println("Entrei no método PresentationService.getPresentationFromCache");
        Presentation cachedPresentation = cacheService.getFromCache(key);

        if (cachedPresentation != null) { 
            System.out.println("Cache: " + cachedPresentation.toString());
            return cachedPresentation;
        }

        Presentation presentation = requestPresentationById(key);

        System.out.println("DB: " + presentation.toString());

        cacheService.putInCache(key, presentation);

        System.out.println("Objeto incluído no cache: {key: " + key +", objeto: " + presentation.toString() + "}");

        return presentation;
    }
}
