package com.redhat.application.service;

import java.util.List;

import com.redhat.domain.model.Presentation;
import com.redhat.domain.repository.PresentationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PresentationService {
    @Inject
    PresentationRepository repository;

    public List<Presentation> findAllPresentations() {
        return repository.findAll().list();
    }
    
    public Presentation findPresentationById(Long id) throws InterruptedException {
        simulateHighLatency();
        
        return repository.findById(id);
    }

    @Transactional
    public Presentation createPresentation(Presentation presentation) {
        repository.persist(presentation);
        return presentation;
    }

    @Transactional
    public boolean deletePresentationById(Long id) {
         return repository.deleteById(id);
    }

    public void simulateHighLatency() throws InterruptedException {
        Thread.sleep(8000);
    }
}
