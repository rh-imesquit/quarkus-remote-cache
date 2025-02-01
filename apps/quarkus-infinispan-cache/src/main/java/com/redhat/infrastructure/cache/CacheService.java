package com.redhat.infrastructure.cache;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import com.redhat.domain.model.Presentation;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

@ApplicationScoped
public class CacheService {
    private static final String CACHE_NAME = "presentations";

    @Inject
    RemoteCacheManager cacheManager;

    /**
     * Retorna o cache de apresentações, garantindo que ele esteja inicializado.
     */
    private RemoteCache<String, Presentation> getCache() {
        return cacheManager.getCache(CACHE_NAME);
    }

    /**
     * Obtém um objeto do cache pelo ID.
     */
    public Presentation getFromCache(Long key) {
        return getCache().get(String.valueOf(key));
    }

    /**
     * Adiciona um objeto ao cache.
     */
    public void putInCache(Long key, Presentation presentation) {
        getCache().put(String.valueOf(key), presentation);
    }
}