package com.redhat.domain.repository;

import com.redhat.domain.model.Presentation;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PresentationRepository  implements PanacheRepository<Presentation> {
    public Presentation findById(Long id) {
        return find("id", id).firstResult();
    }
}
