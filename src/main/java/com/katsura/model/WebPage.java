package com.katsura.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@Data
public class WebPage {
    
    public enum PageType {
        song, playlist, playlists;
    }
    
    public enum Status {
        crawled, uncrawl;
    }

    @Id
    private String url;
    private String title;
    
    @Enumerated(EnumType.STRING)
    private PageType type;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Transient
    private String html;
    
    public WebPage() {
        super();
    }
    
    public WebPage(String url, PageType type) {
        super();
        this.url = url;
        this.type = type;
        this.status = Status.uncrawl;
    }
    
    public WebPage(String url, PageType type, String title) {
        super();
        this.url = url;
        this.type = type;
        this.title = title;
        this.status = Status.uncrawl;
    }

}
