package com.redhat.application.service;

import java.util.ArrayList;
import java.util.List;

import com.redhat.domain.model.Presentation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PresentationArrayService {
    List<Presentation> list = new ArrayList<>();

    public List<Presentation> findAllPresentations() {
        return list.stream().toList();
    }

    public Presentation findPresentationById(Long id) throws InterruptedException {
        simulateHighLatency();
        
        return list.stream()
                   .filter(p -> p.getId().equals(id))
                   .findFirst()
                   .orElse(null);
    }

    public Presentation createPresentation(Presentation presentation) {
        if (presentation.getId() == null) {
            presentation.setId((long) (list.size() + 1));
        }
        list.add(presentation);
        return presentation;
    }

    public boolean deletePresentationById(Long id) {
        return list.removeIf(p -> p.getId().equals(id));
    }

    private void simulateHighLatency() throws InterruptedException {
        Thread.sleep(8000);
    }
}