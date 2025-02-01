package com.redhat.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Presentation implements Serializable {
    private static final long serialVersionUID = 1L; 
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("theme")
    private String theme;
    
    @JsonProperty("dateTime")
    private LocalDateTime dateTime;

    public Presentation () {

    }

    public Presentation(Long id, String author, String theme, LocalDateTime dateTime) {
        this.id = id;
        this.author = author;
        this.theme = theme;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
}
